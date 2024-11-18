package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import main.Board;
import main.GamePanel;
import main.Type;

/**
 * Représente une pièce d'échecs générique.
 */
public class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;
    public boolean moved, twoStepped;

    /**
     * Constructeur de la pièce.
     *
     * @param color Couleur de la pièce (0 pour blanc, 1 pour noir)
     * @param col Colonne initiale
     * @param row Ligne initiale
     */
    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        this.x = getX(col);
        this.y = getY(row);
        preCol = col;
        preRow = row;
    }

    /**
     * Charge l'image de la pièce.
     *
     * @param imagePath Chemin de l'image
     * @return BufferedImage de la pièce
     */
    public BufferedImage getImage(String imagePath) {
        String file = "/pieces/" + imagePath + ".png";
        BufferedImage loadedImage = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream(file);
            if (inputStream != null) {
                loadedImage = ImageIO.read(inputStream);
            } else {
                System.err.println("L'image n'a pas pu charger: " + file);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image: " + file);
        }
        return loadedImage;
    }

    /**
     * Calcule la coordonnée X à partir de la colonne.
     */
    public final int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    /**
     * Calcule la coordonnée Y à partir de la ligne.
     */
    public final int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    /**
     * Calcule la colonne à partir de la coordonnée X.
     */
    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    /**
     * Calcule la ligne à partir de la coordonnée Y.
     */
    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    /**
     * Retourne l'index de la pièce dans la liste des pièces simulées.
     */
    public int getIndex() {
        return GamePanel.simPieces.indexOf(this);
    }

    /**
     * Met à jour la position de la pièce après un mouvement.
     */
    public void updatePosition() {

        // To check en passant
        if (type == Type.PAWN && Math.abs(row - preRow) == 2) {
            twoStepped = true;
        }

        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }

    /**
     * Vérifie si la pièce peut se déplacer vers une case cible. À implémenter
     * dans les classes filles.
     */
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    /**
     * Vérifie si la case cible est dans les limites du plateau.
     */
    public boolean isWithinBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

    /**
     * Vérifie si la case cible est la même que la case actuelle.
     */
    public boolean isSameSquare(int targetCol, int targetRow) {
        return targetCol == preCol && targetRow == preRow;
    }

    /**
     * Dessine la pièce sur le plateau.
     */
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    /**
     * Retourne la pièce présente sur la case cible, s'il y en a une.
     */
    public Piece getHittingP(int targetCol, int targetRow) {
        return GamePanel.simPieces.stream()
                .filter(piece -> piece.col == targetCol && piece.row == targetRow && piece != this)
                .findFirst()
                .orElse(null);
    }

    /**
     * Vérifie si la case cible est valide pour un déplacement.
     */
    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingP = getHittingP(targetCol, targetRow);
        if (hittingP == null) {
            return true;
        } else if (hittingP.color != this.color) {
            return true;
        } else {
            hittingP = null;
            return false;
        }
    }

    /**
     * Vérifie s'il y a une pièce sur la ligne droite entre la position actuelle
     * et la cible.
     */
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {

        //moving left
        for (int c = preCol - 1; c > targetCol; c--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        //moving right
        for (int c = preCol + 1; c < targetCol; c++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == c && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        //moving up
        for (int r = preRow - 1; r > targetRow; r--) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        //moving down
        for (int r = preRow + 1; r < targetRow; r++) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == r) {
                    hittingP = piece;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Vérifie s'il y a une pièce sur la diagonale entre la position actuelle et
     * la cible.
     */
    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        if (targetRow < preRow) {
            // Up left
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }

            // Up right
            for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        if (targetRow > preRow) {
            // Down left
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }

            // Down right
            for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Réinitialise la position de la pièce à sa position précédente.
     */
    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }
}
