package piece;

import main.GamePanel;
import main.Type;

/**
 * Représente un fou dans le jeu d'échecs. Le fou se déplace en diagonale sur
 * n'importe quelle distance.
 */
public class Bishop extends Piece {

    /**
     * Constructeur pour créer un fou.
     *
     * @param color La couleur du fou (blanc ou noir)
     * @param col La colonne initiale du fou sur l'échiquier
     * @param row La ligne initiale du fou sur l'échiquier
     */
    public Bishop(int color, int col, int row) {
        super(color, col, row);

        type = Type.BISHOP;

        // Charge l'image appropriée selon la couleur du fou
        image = getImage(color == GamePanel.WHITE ? "w-bishop" : "b-bishop");
    }

    /**
     * Vérifie si le fou peut se déplacer vers la case cible.
     * 
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement est valide, false sinon
     */
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Vérifier si la case cible est dans les limites du plateau et si elle est la même que la case actuelle
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
            // Vérifier si le mouvement est diagonal
            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                // Vérifier si le mouvement est valide et s'il n'y a pas de pièce sur la diagonale
                if (isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
                    return true;
                }
            }
        }
        return false;
    }
}
