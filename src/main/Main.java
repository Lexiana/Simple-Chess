package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Classe principale qui initialise et lance le jeu d'échecs.
 */
public class Main {

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        // Exécute la création et l'affichage de l'interface graphique dans l'EDT
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    /**
     * Crée et affiche l'interface graphique du jeu.
     */
    private static void createAndShowGUI() {
        // Création de la fenêtre principale
        JFrame window = new JFrame("Simple Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Création et ajout du panneau de jeu à la fenêtre
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        // Ajuste la taille de la fenêtre au contenu
        window.pack();

        // Centre la fenêtre sur l'écran
        window.setLocationRelativeTo(null);

        // Rend la fenêtre visible
        window.setVisible(true);

        // Lance le jeu
        gamePanel.launchGame();
    }
}
