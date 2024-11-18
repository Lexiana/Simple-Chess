package piece;

import main.GamePanel;
import main.Type;

/**
 * Représente un pion dans le jeu d'échecs. Le pion a des règles de mouvement
 * uniques, incluant la possibilité de se déplacer de deux cases au premier
 * coup, la capture en diagonale, et la prise en passant.
 */
public class Pawn extends Piece {

    /**
     * Constructeur pour créer un pion.
     *
     * @param color La couleur du pion (blanc ou noir)
     * @param col La colonne initiale du pion sur l'échiquier
     * @param row La ligne initiale du pion sur l'échiquier
     */
    public Pawn(int color, int col, int row) {
        super(color, col, row);
        type = Type.PAWN;
        image = getImage(color == GamePanel.WHITE ? "w-pawn" : "b-pawn");
    }

    /**
     * Vérifie si le pion peut se déplacer vers la case cible.
     *
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement est valide, false sinon
     */
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            int moveDirection = (color == GamePanel.WHITE) ? -1 : 1;
            hittingP = getHittingP(targetCol, targetRow);

            if (isForwardMove(targetCol, targetRow, moveDirection)) {
                return true;
            }

            if (isDiagonalCapture(targetCol, targetRow, moveDirection)) {
                return true;
            }

            if (isEnPassant(targetCol, targetRow, moveDirection)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si le mouvement est un déplacement en avant valide.
     */
    private boolean isForwardMove(int targetCol, int targetRow, int moveDirection) {
        // Déplacement d'une case
        if (targetCol == preCol && targetRow == preRow + moveDirection && hittingP == null) {
            return true;
        }
        // Déplacement de deux cases (premier mouvement)
        return targetCol == preCol && targetRow == preRow + moveDirection * 2
                && hittingP == null && !moved && !pieceIsOnStraightLine(targetCol, targetRow);
    }

    /**
     * Vérifie si le mouvement est une capture en diagonale valide.
     */
    private boolean isDiagonalCapture(int targetCol, int targetRow, int moveDirection) {
        return Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveDirection
                && hittingP != null && hittingP.color != this.color;
    }

    /**
     * Vérifie si le mouvement est une prise en passant valide.
     */
    private boolean isEnPassant(int targetCol, int targetRow, int moveDirection) {
        if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveDirection) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == preRow
                        && piece.type == Type.PAWN && piece.twoStepped) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        return false;
    }
}
