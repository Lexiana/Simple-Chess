package piece;

import main.GamePanel;
import main.Type;

/**
 * Représente un roi dans le jeu d'échecs. Le roi peut se déplacer d'une case
 * dans n'importe quelle direction et effectuer un roque.
 */
public class King extends Piece {

    /**
     * Constructeur pour créer un roi.
     *
     * @param color La couleur du roi (blanc ou noir)
     * @param col La colonne initiale du roi sur l'échiquier
     * @param row La ligne initiale du roi sur l'échiquier
     */
    public King(int color, int col, int row) {
        super(color, col, row);
        type = Type.KING;
        image = getImage(color == GamePanel.WHITE ? "w-king" : "b-king");
    }

    /**
     * Vérifie si le roi peut se déplacer vers la case cible.
     *
     * @param targetCol La colonne de la case cible
     * @param targetRow La ligne de la case cible
     * @return true si le mouvement est valide, false sinon
     */
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {

            // Movement normal ru roi
            if (isNormalKingMove(targetCol, targetRow) && isValidSquare(targetCol, targetRow)) {
                return true;
            }

            // Roque
            if (!moved) {
                if (canCastleRight(targetCol, targetRow)) {
                    return true;
                }
                if (canCastleLeft(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si le mouvement est un déplacement normal du roi (une case dans
     * n'importe quelle direction).
     */
    private boolean isNormalKingMove(int targetCol, int targetRow) {
        int colDiff = Math.abs(targetCol - preCol);
        int rowDiff = Math.abs(targetRow - preRow);
        return (colDiff + rowDiff == 1) || (colDiff * rowDiff == 1);
    }

    /**
     * Vérifie si le roi peut effectuer un roque vers la droite.
     */
    private boolean canCastleRight(int targetCol, int targetRow) {
        if (targetCol == preCol + 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == preCol + 3 && piece.row == preRow && !piece.moved) {
                    GamePanel.castlingP = piece;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si le roi peut effectuer un roque vers la gauche.
     */
    private boolean canCastleLeft(int targetCol, int targetRow) {
        if (targetCol == preCol - 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
            Piece[] p = new Piece[2];
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == preCol - 3 && piece.row == targetRow) {
                    p[0] = piece;
                }
                if (piece.col == preCol - 4 && piece.row == targetRow) {
                    p[1] = piece;
                }
            }
            if (p[0] == null && p[1] != null && !p[1].moved) {
                GamePanel.castlingP = p[1];
                return true;
            }
        }
        return false;
    }
}
