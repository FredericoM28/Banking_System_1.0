package controller;

/*public class App {
    public static void main(String[] args) throws Exception {


        SistemaController execucao = new SistemaController();
        System.out.println("Sistema bancario rodando perfeitamente" + execucao);

    }
}*/

import javax.swing.SwingUtilities;

import view.PainelFuncAtendimento;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PainelFuncAtendimento(new SistemaController(), "Funcionário", "Atendimento").setVisible(true);
        });
    }
}