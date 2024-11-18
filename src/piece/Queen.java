package piece;

import main.GamePanel;
import main.Type;

/**
 * Représente une reine dans le jeu d'échecs. La reine peut se déplacer dans
 * n'importe quelle direction (horizontale, verticale, diagonale) sur n'importe
 * quelle distance.
 */
public class Queen extends Piece {

    /**
     * Constructeur pour créer une reine.
     *
     * @param color La couleur de la reine (blanc ou noir)
     * @param col La colonne initiale de la reine sur l'échiquier
     * @param row La ligne initiale de la reine sur l'échiquier
     */
    public Queen(int color, int col, int row) {
        super(color, col, row);
        type = Type.QUEEN;
        image = getImage(color == GamePanel.WHITE ? "w-queen" : "b-queen");
    }

    /**
     * Vérifie si la reine peut se déplacer vers la case cible.
     *
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement est valide, false sinon
     */
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            if (isStraightMove(targetCol, targetRow) || isDiagonalMove(targetCol, targetRow)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si le mouvement est un déplacement en ligne droite (vertical ou
     * horizontal) valide.
     *
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement en ligne droite est valide, false sinon
     */
    private boolean isStraightMove(int targetCol, int targetRow) {
        return (targetCol == preCol || targetRow == preRow)
                && isValidSquare(targetCol, targetRow)
                && !pieceIsOnStraightLine(targetCol, targetRow);
    }

    /**
     * Vérifie si le mouvement est un déplacement en diagonale valide.
     *
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement en diagonale est valide, false sinon
     */
    private boolean isDiagonalMove(int targetCol, int targetRow) {
        return Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)
                && isValidSquare(targetCol, targetRow)
                && !pieceIsOnDiagonalLine(targetCol, targetRow);
    }
}
