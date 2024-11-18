package piece;

import main.GamePanel;
import main.Type;

/**
 * Représente une tour dans le jeu d'échecs. La tour peut se déplacer
 * horizontalement ou verticalement sur n'importe quelle distance.
 */
public class Rook extends Piece {

    /**
     * Constructeur pour créer une tour.
     *
     * @param color La couleur de la tour (blanc ou noir)
     * @param col La colonne initiale de la tour sur l'échiquier
     * @param row La ligne initiale de la tour sur l'échiquier
     */
    public Rook(int color, int col, int row) {
        super(color, col, row);
        type = Type.ROOK;
        image = getImage(color == GamePanel.WHITE ? "w-rook" : "b-rook");
    }

    /**
     * Vérifie si la tour peut se déplacer vers la case cible.
     *
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement est valide, false sinon
     */
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            return isStraightMove(targetCol, targetRow);
        }
        return false;
    }

    /**
     * Vérifie si le mouvement est un déplacement en ligne droite (horizontal ou
     * vertical) valide.
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
}
