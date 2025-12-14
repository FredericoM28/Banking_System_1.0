import pandas as pd
import numpy as np
from sqlalchemy import create_engine
from dash import Dash, dcc, html, Input, Output, State, callback_context
import plotly.express as px
import plotly.graph_objects as go
from plotly.subplots import make_subplots
from datetime import datetime, timedelta

# ==========================
# CONFIGURAÇÃO DO BANCO DE DADOS
# ==========================
DB_USER = "root"
DB_PASSWORD = ""
DB_HOST = "localhost"
DB_NAME = "bancoDATABSE"

engine = create_engine(f"mysql+mysqlconnector://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}")

# ==========================
# FUNÇÃO DE CONSULTA
# ==========================
def carregar_dados(tabela):
    query = f"SELECT * FROM {tabela}"
    return pd.read_sql(query, con=engine)

# Carregar dados
try:
    clientes = carregar_dados("cliente")
    contas = carregar_dados("conta")
    transacoes = carregar_dados("transacao")
    emprestimos = carregar_dados("emprestimo")
    print("✅ Dados carregados com sucesso!")
except Exception as e:
    print(f"❌ Erro ao carregar dados: {e}")
    # Criar dados de exemplo para teste
    clientes = pd.DataFrame({
        'idCliente': [1, 2, 3],
        'nome': ['Cliente A', 'Cliente B', 'Cliente C'],
        'status': ['ATIVO', 'ATIVO', 'INATIVO'],
        'idade': ['1990-01-01', '1985-05-15', '1978-12-30']
    })
    contas = pd.DataFrame({
        'idConta': [1, 2, 3],
        'idCliente': [1, 2, 3],
        'tipoConta': ['CORRENTE', 'POUPANCA', 'CORRENTE'],
        'saldo': [1000.00, 2500.50, 500.00]
    })
    transacoes = pd.DataFrame({
        'idTransacao': [1, 2, 3, 4],
        'contaOrigemId': [1, 2, 1, 3],
        'tipoTransacao': ['TRANSFERENCIA', 'DEPOSITO', 'SAQUE', 'TRANSFERENCIA'],
        'valor': [100.00, 500.00, 50.00, 200.00],
        'dataTransacao': ['2024-01-01', '2024-01-02', '2024-01-03', '2024-01-04'],
        'status': ['CONCLUIDA', 'CONCLUIDA', 'CONCLUIDA', 'CONCLUIDA']
    })
    emprestimos = pd.DataFrame({
        'idEmprestimo': [1, 2, 3],
        'idCliente': [1, 2, 3],
        'valorSolicitado': [5000.00, 10000.00, 3000.00],
        'status': ['APROVADO', 'PENDENTE', 'NEGADO'],
        'dataSolicitacao': ['2024-01-01', '2024-01-02', '2024-01-03']
    })

# ==========================
# PREPARAÇÃO DE DADOS AVANÇADA - CORRIGIDA
# ==========================
# Converter datas
transacoes["dataTransacao"] = pd.to_datetime(transacoes["dataTransacao"])
clientes["idade"] = pd.to_datetime(clientes["idade"])

# Calcular idade em anos - COM TRATAMENTO DE ERRO
try:
    clientes["idade_anos"] = ((pd.Timestamp.now() - clientes["idade"]).dt.days / 365.25).astype(int)
except Exception as e:
    print(f"⚠️ Erro ao calcular idade: {e}")
    # Idades padrão para teste
    clientes["idade_anos"] = [35, 42, 28, 51, 39]

# Merge para análises relacionadas
contas_clientes = pd.merge(contas, clientes, left_on="idCliente", right_on="idCliente", how="left")

# CRIAR faixa_etaria NO contas_clientes também
try:
    faixa_etaria = pd.cut(contas_clientes["idade_anos"], 
                         bins=[0, 25, 35, 45, 55, 65, 100], 
                         labels=["<25", "25-35", "35-45", "45-55", "55-65", "65+"])
    contas_clientes["faixa_etaria"] = faixa_etaria
    saldo_faixa_etaria = contas_clientes.groupby("faixa_etaria")["saldo"].sum().reset_index()
except Exception as e:
    print(f"⚠️ Erro ao criar faixa etária: {e}")
    # Dados de exemplo para faixa etária
    saldo_faixa_etaria = pd.DataFrame({
        'faixa_etaria': ['<25', '25-35', '35-45', '45-55', '55-65', '65+'],
        'saldo': [5000, 15000, 25000, 18000, 12000, 8000]
    })

# Preparar transações completo
try:
    transacoes_completo = pd.merge(transacoes, contas, left_on="contaOrigemId", right_on="idConta", how="left")
    transacoes_completo = pd.merge(transacoes_completo, clientes, left_on="idCliente", right_on="idCliente", how="left")
except Exception as e:
    print(f"⚠️ Erro ao merge transações: {e}")
    transacoes_completo = transacoes.copy()

# Análises temporais
try:
    transacoes["mes"] = transacoes["dataTransacao"].dt.to_period("M")
    transacoes_mensais = transacoes.groupby("mes").agg({
        "valor": ["sum", "count"],
        "idTransacao": "count"
    }).reset_index()
    transacoes_mensais.columns = ["Mes", "Valor_Total", "Quantidade", "Count"]
    transacoes_mensais["Mes"] = transacoes_mensais["Mes"].astype(str)
except Exception as e:
    print(f"⚠️ Erro na análise temporal: {e}")
    transacoes_mensais = pd.DataFrame({
        "Mes": ["2024-01", "2024-02", "2024-03"],
        "Valor_Total": [10000, 15000, 12000],
        "Quantidade": [50, 75, 60],
        "Count": [50, 75, 60]
    })

print("✅ Preparação de dados concluída!")

# ==========================
# ESTILOS CSS PERSONALIZADOS
# ==========================
external_stylesheets = ['https://codepen.io/chriddyp/pen/bWLwgP.css']

styles = {
    'header': {
        'backgroundColor': '#2E86AB',
        'padding': '20px',
        'borderRadius': '10px',
        'color': 'white',
        'textAlign': 'center',
        'marginBottom': '20px'
    },
    'card': {
        'backgroundColor': '#1E1E1E',
        'padding': '15px',
        'borderRadius': '10px',
        'border': '1px solid #444',
        'color': 'white',
        'marginBottom': '10px'
    },
    'metric': {
        'fontSize': '24px',
        'fontWeight': 'bold',
        'color': '#00CC96'
    },
    'filter': {
        'backgroundColor': '#2D2D2D',
        'padding': '15px',
        'borderRadius': '10px',
        'marginBottom': '20px'
    },
    'tab': {
        'backgroundColor': '#2D2D2D',
        'padding': '10px',
        'borderRadius': '5px'
    }
}

# ==========================
# DASHBOARD AVANÇADO
# ==========================
app = Dash(__name__, external_stylesheets=external_stylesheets, suppress_callback_exceptions=True)
app.title = "🏦 Dashboard Bancário Inteligente"

# ==========================
# LAYOUT PRINCIPAL
# ==========================
app.layout = html.Div([
    # Header
    html.Div([
        html.H1("🏦 Dashboard Bancário Inteligente", 
                style={'color': 'white', 'margin': '0', 'fontWeight': 'bold'}),
        html.P("Análise em tempo real dos dados bancários", 
               style={'color': '#CCCCCC', 'margin': '5px 0 0 0'})
    ], style=styles['header']),
    
    # Cards de Métricas
    html.Div([
        html.Div([
            html.Div([
                html.H4("👥 Clientes Ativos", style={'color': 'white', 'margin': '0'}),
                html.H2(f"{clientes[clientes['status']=='ATIVO'].shape[0]:,}", 
                       style=styles['metric']),
                html.P(f"Total: {clientes.shape[0]:,} clientes", 
                      style={'color': '#CCCCCC', 'margin': '0'})
            ], style=styles['card'])
        ], className="three columns"),
        
        html.Div([
            html.Div([
                html.H4("💰 Saldo Total", style={'color': 'white', 'margin': '0'}),
                html.H2(f"${contas['saldo'].sum():,.2f}", 
                       style=styles['metric']),
                html.P(f"Média: ${contas['saldo'].mean():,.2f}", 
                      style={'color': '#CCCCCC', 'margin': '0'})
            ], style=styles['card'])
        ], className="three columns"),
        
        html.Div([
            html.Div([
                html.H4("🔄 Transações", style={'color': 'white', 'margin': '0'}),
                html.H2(f"{transacoes.shape[0]:,}", 
                       style=styles['metric']),
                html.P(f"Hoje: {transacoes[transacoes['dataTransacao'].dt.date == datetime.today().date()].shape[0]}", 
                      style={'color': '#CCCCCC', 'margin': '0'})
            ], style=styles['card'])
        ], className="three columns"),
        
        html.Div([
            html.Div([
                html.H4("🏦 Empréstimos", style={'color': 'white', 'margin': '0'}),
                html.H2(f"${emprestimos['valorSolicitado'].sum():,.2f}", 
                       style=styles['metric']),
                html.P(f"Ativos: {emprestimos[emprestimos['status']=='APROVADO'].shape[0]}", 
                      style={'color': '#CCCCCC', 'margin': '0'})
            ], style=styles['card'])
        ], className="three columns"),
    ], className="row", style={'marginBottom': '20px'}),
    
    # Filtros
    html.Div([
        html.Div([
            html.Label("🏷️ Tipo de Transação:", style={'color': 'white', 'fontWeight': 'bold'}),
            dcc.Dropdown(
                id='filtro-transacao-tipo',
                options=[{"label": "Todos", "value": "all"}] + 
                        [{"label": t, "value": t} for t in transacoes["tipoTransacao"].unique()],
                value="all",
                multi=True,
                style={'backgroundColor': '#333', 'color': 'white'}
            )
        ], className="three columns"),
        
        html.Div([
            html.Label("👤 Status Cliente:", style={'color': 'white', 'fontWeight': 'bold'}),
            dcc.Dropdown(
                id='filtro-cliente-status',
                options=[{"label": "Todos", "value": "all"}] + 
                        [{"label": s, "value": s} for s in clientes["status"].unique()],
                value="all",
                style={'backgroundColor': '#333', 'color': 'white'}
            )
        ], className="three columns"),
        
        html.Div([
            html.Label("📊 Tipo de Conta:", style={'color': 'white', 'fontWeight': 'bold'}),
            dcc.Dropdown(
                id='filtro-conta-tipo',
                options=[{"label": "Todos", "value": "all"}] + 
                        [{"label": t, "value": t} for t in contas["tipoConta"].unique()],
                value="all",
                style={'backgroundColor': '#333', 'color': 'white'}
            )
        ], className="three columns"),
        
        html.Div([
            html.Label("📈 Agrupamento Temporal:", style={'color': 'white', 'fontWeight': 'bold'}),
            dcc.Dropdown(
                id='filtro-agrupamento',
                options=[
                    {"label": "Diário", "value": "D"},
                    {"label": "Semanal", "value": "W"},
                    {"label": "Mensal", "value": "M"},
                    {"label": "Trimestral", "value": "Q"}
                ],
                value="M",
                style={'backgroundColor': '#333', 'color': 'white'}
            )
        ], className="three columns"),
    ], className="row", style=styles['filter']),
    
    # Date Picker
    html.Div([
        html.Label("📅 Período de Análise:", style={'color': 'white', 'fontWeight': 'bold', 'marginRight': '10px'}),
        dcc.DatePickerRange(
            id='date-picker-range',
            start_date=transacoes['dataTransacao'].min(),
            end_date=transacoes['dataTransacao'].max(),
            display_format='YYYY-MM-DD',
            style={'backgroundColor': '#333'}
        )
    ], style={'marginBottom': '20px', 'padding': '10px', 'backgroundColor': '#2D2D2D', 'borderRadius': '10px'}),
    
    # Tabs - AGORA COM TODOS OS GRÁFICOS NO LAYOUT INICIAL
    dcc.Tabs(id="tabs-principais", value='tab-1', children=[
        dcc.Tab(label='📊 Visão Geral', value='tab-1', style=styles['tab']),
        dcc.Tab(label='📈 Análise Temporal', value='tab-2', style=styles['tab']),
        dcc.Tab(label='💳 Detalhes Transações', value='tab-3', style=styles['tab']),
    ], colors={
        "border": "none",
        "primary": "#2E86AB",
        "background": "#2D2D2D"
    }),
    
    # CONTEÚDO DAS TABS - AGORA FIXO NO LAYOUT
    html.Div(id='conteudo-tabs', children=[
        # Tab 1 - Visão Geral (sempre visível)
        html.Div([
            html.Div([
                html.Div([
                    dcc.Graph(id='grafico-clientes-status')
                ], className="six columns"),
                html.Div([
                    dcc.Graph(id='grafico-tipos-conta')
                ], className="six columns"),
            ], className="row"),
            html.Div([
                html.Div([
                    dcc.Graph(id='grafico-transacoes-tipo')
                ], className="six columns"),
                html.Div([
                    dcc.Graph(id='grafico-transacoes-tempo')
                ], className="six columns"),
            ], className="row"),
            html.Div([
                html.Div([
                    dcc.Graph(id='grafico-emprestimos-status')
                ], className="six columns"),
                html.Div([
                    dcc.Graph(id='grafico-saldo-faixa-etaria')
                ], className="six columns"),
            ], className="row"),
        ], id='tab-1-content', style={'display': 'block'}),
        
        # Tab 2 - Análise Temporal (inicialmente oculta)
        html.Div([
            html.Div([
                dcc.Graph(id='grafico-evolucao-mensal')
            ], className="twelve columns"),
            html.Div([
                html.Div([
                    dcc.Graph(id='grafico-comparativo-tempo')
                ], className="six columns"),
                html.Div([
                    dcc.Graph(id='grafico-sazonalidade')
                ], className="six columns"),
            ], className="row"),
        ], id='tab-2-content', style={'display': 'none'}),
        
        # Tab 3 - Detalhes Transações (inicialmente oculta)
        html.Div([
            html.Div([
                html.Div([
                    html.H4("📋 Últimas Transações", style={'color': 'white'}),
                    html.Div(id='tabela-transacoes')
                ], style=styles['card'])
            ], className="twelve columns"),
            html.Div([
                html.Div([
                    dcc.Graph(id='grafico-transacoes-categoria')
                ], className="six columns"),
                html.Div([
                    dcc.Graph(id='grafico-valor-medio')
                ], className="six columns"),
            ], className="row"),
        ], id='tab-3-content', style={'display': 'none'}),
    ]),
    
    html.Footer([
        html.Hr(style={'borderColor': '#444'}),
        html.P("Desenvolvido por Frederico — Dashboard Bancário Inteligente", 
              style={'textAlign': 'center', 'color': 'gray', 'marginTop': '20px'})
    ], style={'marginTop': '40px'})
], style={'backgroundColor': '#1E1E1E', 'padding': '20px', 'minHeight': '100vh'})

# ==========================
# CALLBACK PARA MOSTRAR/OCULTAR TABS
# ==========================
@app.callback(
    [Output('tab-1-content', 'style'),
     Output('tab-2-content', 'style'),
     Output('tab-3-content', 'style')],
    [Input('tabs-principais', 'value')]
)
def mostrar_tab_conteudo(aba_selecionada):
    styles = [{'display': 'none'}, {'display': 'none'}, {'display': 'none'}]
    
    if aba_selecionada == 'tab-1':
        styles[0] = {'display': 'block'}
    elif aba_selecionada == 'tab-2':
        styles[1] = {'display': 'block'}
    elif aba_selecionada == 'tab-3':
        styles[2] = {'display': 'block'}
    
    return styles

# ==========================
# CALLBACK PRINCIPAL PARA ATUALIZAR TODOS OS GRÁFICOS
# ==========================
@app.callback(
    [Output('grafico-clientes-status', 'figure'),
     Output('grafico-tipos-conta', 'figure'),
     Output('grafico-transacoes-tipo', 'figure'),
     Output('grafico-transacoes-tempo', 'figure'),
     Output('grafico-emprestimos-status', 'figure'),
     Output('grafico-saldo-faixa-etaria', 'figure'),
     Output('grafico-evolucao-mensal', 'figure'),
     Output('grafico-comparativo-tempo', 'figure'),
     Output('grafico-sazonalidade', 'figure'),
     Output('grafico-transacoes-categoria', 'figure'),
     Output('grafico-valor-medio', 'figure'),
     Output('tabela-transacoes', 'children')],
    [Input('filtro-transacao-tipo', 'value'),
     Input('filtro-cliente-status', 'value'),
     Input('filtro-conta-tipo', 'value'),
     Input('filtro-agrupamento', 'value'),
     Input('date-picker-range', 'start_date'),
     Input('date-picker-range', 'end_date')]
)
def atualizar_dashboard(filtro_tipo, filtro_status, filtro_conta, agrupamento, start_date, end_date):
    # Aplicar filtros
    df_transacoes = transacoes_completo.copy()
    df_clientes = clientes.copy()
    df_contas = contas.copy()
    
    # Filtro de data
    if start_date and end_date:
        df_transacoes = df_transacoes[
            (df_transacoes['dataTransacao'] >= pd.to_datetime(start_date)) & 
            (df_transacoes['dataTransacao'] <= pd.to_datetime(end_date))
        ]
    
    # Filtro tipo transação
    if filtro_tipo != "all" and filtro_tipo:
        if isinstance(filtro_tipo, list):
            df_transacoes = df_transacoes[df_transacoes["tipoTransacao"].isin(filtro_tipo)]
        else:
            df_transacoes = df_transacoes[df_transacoes["tipoTransacao"] == filtro_tipo]
    
    # Filtro status cliente
    if filtro_status != "all":
        df_clientes = df_clientes[df_clientes["status"] == filtro_status]
        clientes_filtrados_ids = df_clientes["idCliente"].unique()
        df_contas = df_contas[df_contas["idCliente"].isin(clientes_filtrados_ids)]
        df_transacoes = df_transacoes[df_transacoes["idCliente"].isin(clientes_filtrados_ids)]
    
    # Filtro tipo conta
    if filtro_conta != "all":
        df_contas = df_contas[df_contas["tipoConta"] == filtro_conta]
        contas_filtradas_ids = df_contas["idConta"].unique()
        df_transacoes = df_transacoes[df_transacoes["contaOrigemId"].isin(contas_filtradas_ids)]
    
    # Preparar dados filtrados
    clientes_status_count = df_clientes["status"].value_counts().reset_index()
    clientes_status_count.columns = ["Status", "Quantidade"]
    
    tipos_conta_count = df_contas["tipoConta"].value_counts().reset_index()
    tipos_conta_count.columns = ["Tipo_Conta", "Quantidade"]
    
    transacoes_tipo_count = df_transacoes["tipoTransacao"].value_counts().reset_index()
    transacoes_tipo_count.columns = ["Tipo_Transacao", "Quantidade"]
    
    # Agrupamento temporal
    df_transacoes_temp = df_transacoes.copy()
    df_transacoes_temp['dataTransacao'] = pd.to_datetime(df_transacoes_temp['dataTransacao'])
    try:
        transacoes_tempo = df_transacoes_temp.groupby(
            df_transacoes_temp['dataTransacao'].dt.to_period(agrupamento)
        )['valor'].sum().reset_index()
        transacoes_tempo['dataTransacao'] = transacoes_tempo['dataTransacao'].astype(str)
    except:
        # Fallback para agrupamento mensal
        transacoes_tempo = df_transacoes_temp.groupby(
            df_transacoes_temp['dataTransacao'].dt.to_period('M')
        )['valor'].sum().reset_index()
        transacoes_tempo['dataTransacao'] = transacoes_tempo['dataTransacao'].astype(str)
    
    # Configuração de tema escuro para gráficos
    template_dark = 'plotly_dark'
    
    # Gráficos
    fig_clientes_status = px.pie(clientes_status_count, names="Status", values="Quantidade",
                                title="👥 Distribuição de Clientes por Status",
                                color_discrete_sequence=px.colors.qualitative.Set3,
                                template=template_dark)
    fig_clientes_status.update_traces(textposition='inside', textinfo='percent+label')
    
    fig_tipos_conta = px.bar(tipos_conta_count, x="Tipo_Conta", y="Quantidade", 
                            title="🏦 Tipos de Conta",
                            color="Tipo_Conta",
                            color_discrete_sequence=px.colors.qualitative.Pastel,
                            template=template_dark)
    fig_tipos_conta.update_layout(showlegend=False)
    
    fig_transacoes_tipo = px.bar(transacoes_tipo_count, x="Tipo_Transacao", y="Quantidade",
                                title="💳 Tipos de Transações",
                                color="Tipo_Transacao",
                                color_discrete_sequence=px.colors.qualitative.Vivid,
                                template=template_dark)
    fig_transacoes_tipo.update_layout(showlegend=False)
    
    fig_transacoes_tempo = px.line(transacoes_tempo, x="dataTransacao", y="valor",
                                  title="📈 Transações ao Longo do Tempo",
                                  markers=True,
                                  template=template_dark)
    fig_transacoes_tempo.update_traces(line=dict(width=3))
    
    fig_emprestimos_status = px.pie(emprestimos, names="status", values="valorSolicitado",
                                   title="🏦 Empréstimos por Status e Valor",
                                   color_discrete_sequence=px.colors.qualitative.Set2,
                                   template=template_dark)
    
    fig_saldo_faixa_etaria = px.bar(saldo_faixa_etaria, x="faixa_etaria", y="saldo",
                                   title="👨‍👩‍👧‍👦 Saldo Total por Faixa Etária",
                                   color="faixa_etaria",
                                   color_discrete_sequence=px.colors.sequential.Viridis,
                                   template=template_dark)
    
    # Gráfico de evolução mensal
    try:
        evolucao_mensal = transacoes_mensais.tail(12)
        fig_evolucao_mensal = make_subplots(specs=[[{"secondary_y": True}]])
        fig_evolucao_mensal.add_trace(
            go.Scatter(x=evolucao_mensal["Mes"], y=evolucao_mensal["Valor_Total"],
                      name="Valor Total", line=dict(width=4)),
            secondary_y=False,
        )
        fig_evolucao_mensal.add_trace(
            go.Bar(x=evolucao_mensal["Mes"], y=evolucao_mensal["Quantidade"],
                  name="Quantidade", opacity=0.6),
            secondary_y=True,
        )
        fig_evolucao_mensal.update_layout(
            title_text="📊 Evolução Mensal - Valor vs Quantidade",
            template=template_dark
        )
    except Exception as e:
        fig_evolucao_mensal = px.line(title="📊 Evolução Mensal - Dados não disponíveis", template=template_dark)
    
    # Gráfico comparativo
    try:
        fig_comparativo = px.scatter(df_transacoes, x="valor", y="dataTransacao",
                                    color="tipoTransacao",
                                    title="📊 Distribuição de Valores por Tipo",
                                    opacity=0.7,
                                    template=template_dark)
    except:
        fig_comparativo = px.scatter(title="📊 Distribuição - Dados não disponíveis", template=template_dark)
    
    # Sazonalidade
    try:
        transacoes_dia_semana = transacoes.copy()
        transacoes_dia_semana['dia_semana'] = transacoes_dia_semana['dataTransacao'].dt.day_name()
        sazonalidade_dia = transacoes_dia_semana.groupby('dia_semana')['valor'].mean().reset_index()
        
        # Ordenar dias da semana
        dias_ordenados = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
        sazonalidade_dia['dia_semana'] = pd.Categorical(sazonalidade_dia['dia_semana'], categories=dias_ordenados, ordered=True)
        sazonalidade_dia = sazonalidade_dia.sort_values('dia_semana')
        
        fig_sazonalidade = px.line(sazonalidade_dia, x='dia_semana', y='valor',
                                  title="🔄 Média de Transações por Dia da Semana",
                                  markers=True,
                                  template=template_dark)
    except:
        fig_sazonalidade = px.line(title="🔄 Sazonalidade - Dados não disponíveis", template=template_dark)
    
    # Transações por categoria
    try:
        if 'categoria' in df_transacoes.columns and not df_transacoes['categoria'].isna().all():
            transacoes_categoria = df_transacoes['categoria'].value_counts().reset_index()
            fig_transacoes_categoria = px.pie(transacoes_categoria, values='count', names='categoria',
                                             title="🌳 Transações por Categoria",
                                             template=template_dark)
        else:
            fig_transacoes_categoria = px.pie(values=[100], names=['Sem categoria'], 
                                             title="🌳 Transações por Categoria",
                                             template=template_dark)
    except:
        fig_transacoes_categoria = px.pie(values=[1], names=['Sem dados'], 
                                         title="🌳 Transações por Categoria",
                                         template=template_dark)
    
    # Valor médio por tipo
    try:
        valor_medio_tipo = df_transacoes.groupby('tipoTransacao')['valor'].mean().reset_index()
        fig_valor_medio = px.bar(valor_medio_tipo, x='tipoTransacao', y='valor',
                                title="💰 Valor Médio por Tipo de Transação",
                                color='tipoTransacao',
                                template=template_dark)
    except:
        fig_valor_medio = px.bar(title="💰 Valor Médio - Dados não disponíveis", template=template_dark)
    
    # Tabela de transações
    try:
        tabela_dados = df_transacoes[['dataTransacao', 'tipoTransacao', 'valor', 'status']].head(10).round(2)
        tabela_transacoes = html.Table([
            html.Thead([
                html.Tr([html.Th(col, style={'color': 'white', 'padding': '10px', 'border': '1px solid #444'}) 
                        for col in tabela_dados.columns])
            ]),
            html.Tbody([
                html.Tr([html.Td(tabela_dados.iloc[i][col], 
                                style={'color': 'white', 'padding': '8px', 'border': '1px solid #444'}) 
                        for col in tabela_dados.columns])
                for i in range(len(tabela_dados))
            ])
        ], style={'width': '100%', 'borderCollapse': 'collapse', 'marginTop': '10px'})
    except:
        tabela_transacoes = html.P("Nenhuma transação encontrada com os filtros aplicados.", 
                                 style={'color': 'white', 'textAlign': 'center'})
    
    return (fig_clientes_status, fig_tipos_conta, fig_transacoes_tipo, 
            fig_transacoes_tempo, fig_emprestimos_status, fig_saldo_faixa_etaria,
            fig_evolucao_mensal, fig_comparativo, fig_sazonalidade,
            fig_transacoes_categoria, fig_valor_medio, tabela_transacoes)

# ==========================
if __name__ == "__main__":
    print("🚀 Iniciando Dashboard Bancário...")
    print("📊 Acesse: http://localhost:8050")
    app.run(debug=True, port=8050)