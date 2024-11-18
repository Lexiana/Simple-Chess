package piece;

import main.GamePanel;
import main.Type;

/**
 * Représente un cavalier dans le jeu d'échecs. Le cavalier se déplace en "L",
 * c'est-à-dire deux cases dans une direction et une case perpendiculairement.
 */
public class Knight extends Piece {

    /**
     * Constructeur pour créer un cavalier.
     *
     * @param color La couleur du cavalier (blanc ou noir)
     * @param col La colonne initiale du cavalier sur l'échiquier
     * @param row La ligne initiale du cavalier sur l'échiquier
     */
    public Knight(int color, int col, int row) {
        super(color, col, row);
        type = Type.KNIGHT;
        image = getImage(color == GamePanel.WHITE ? "w-knight" : "b-knight");
    }

    /**
     * Vérifie si le cavalier peut se déplacer vers la case cible.
     *
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement est valide, false sinon
     */
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow)) {
            // Le cavalier peut se déplacer si son mouvement forme un "L" (ratio 1:2 ou 2:1)
            if (isKnightMove(targetCol, targetRow)) {
                return isValidSquare(targetCol, targetRow);
            }
        }
        return false;
    }

    /**
     * Vérifie si le mouvement correspond à un déplacement en "L" du cavalier.
     *
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement forme un "L", false sinon
     */
    private boolean isKnightMove(int targetCol, int targetRow) {
        int colDiff = Math.abs(targetCol - preCol);
        int rowDiff = Math.abs(targetRow - preRow);
        return (colDiff == 2 && rowDiff == 1) || (colDiff == 1 && rowDiff == 2);
    }
}
