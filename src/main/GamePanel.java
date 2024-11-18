package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

/**
 * Classe représentant le panneau de jeu d'échecs. Gère l'affichage du plateau,
 * des pièces et les interactions du joueur.
 */
public class GamePanel extends JPanel implements Runnable {

    // Constantes de configuration
    public static final int PANEL_WIDTH = 1100;
    public static final int PANEL_HEIGHT = 800;
    public static final int FPS = 60;

    // Variables de fonctionnement
    private Thread gameThread;
    private final Board board = new Board();
    private final Mouse mouse = new Mouse();

    // Listes de pièces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();

    // Variables d'état du jeu
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    private int currentColor = WHITE;
    private Piece activeP, checkingP;
    public static Piece castlingP;
    // Variables de jeu

    private boolean validSquare;
    private boolean promotion;
    private boolean stalemate;
    private boolean gameOver;

    /**
     * Constructeur de GamePanel, initialise le panneau de jeu.
     */
    public GamePanel() {
        setupPanel();
        initializeGame();
    }

    /**
     * Configuration du panneau de jeu.
     */
    private void setupPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
    }

    /**
     * Initialise le jeu en plaçant les pièces sur le plateau.
     */
    private void initializeGame() {
        setPieces();
        copyPieces(pieces, simPieces);
    }

    /**
     * Lance le fil de jeu.
     */
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Place les pièces sur le plateau au début du jeu.
     */
    public void setPieces() {
        String[] backRowPieces = {"R", "N", "B", "Q", "K", "B", "N", "R"};
        for (int color = 0; color <= 1; color++) {
            int pawnRow = (color == WHITE) ? 6 : 1;
            int pieceRow = (color == WHITE) ? 7 : 0;

            for (int i = 0; i < 8; i++) {
                pieces.add(new Pawn(color, i, pawnRow));
                pieces.add(createPiece(backRowPieces[i], color, i, pieceRow));
            }
        }
    }

    /**
     * Crée une pièce en fonction de son type.
     *
     * @param type Le type de pièce à créer (Rook, Knight, etc.)
     * @param color La couleur de la pièce (blanc ou noir)
     * @param x La colonne de la pièce sur le plateau
     * @param y La ligne de la pièce sur le plateau
     * @return La pièce créée ou null si le type est invalide
     */
    private Piece createPiece(String type, int color, int x, int y) {
        return switch (type) {
            case "R" ->
                new Rook(color, x, y);
            case "N" ->
                new Knight(color, x, y);
            case "B" ->
                new Bishop(color, x, y);
            case "Q" ->
                new Queen(color, x, y);
            case "K" ->
                new King(color, x, y);
            default ->
                null;
        };
    }

    /**
     * Copie les pièces d'une liste source vers une liste cible.
     *
     * @param source Liste source contenant les pièces à copier
     * @param target Liste cible où les pièces seront copiées
     */
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        if (source != null && target != null) {
            target.clear();
            target.addAll(source);
        }
    }

    /**
     * Boucle principale du jeu.
     */
    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    /**
     * Met à jour l'état du jeu à chaque itération.
     */
    private void update() {
        if (promotion) {
            promoting(); // Gère la promotion d'un pion si nécessaire
        } else if (!gameOver && !stalemate) {
            handleMouseInput(); // Gère les entrées de la souris pour déplacer les pièces
        }
    }

    /**
     * Gère l'entrée de la souris pour déplacer les pièces.
     */
    private void handleMouseInput() {
        if (mouse.pressed) {
            if (activeP == null) {
                selectPiece(); // Sélectionne une pièce si aucune n'est active
            } else {
                simulate(); // Simule un mouvement pour voir s'il est valide
            }
        } else {
            finalizeMove(); // Finalise le mouvement lorsque le bouton est relâché
        }
    }

    /**
     * Sélectionne une pièce si elle appartient au joueur actuel.
     */
    private void selectPiece() {
        for (Piece piece : simPieces) {
            if (piece.color == currentColor && piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                activeP = piece; // Définit la pièce active si elle est sélectionnée par l'utilisateur
            }
        }
    }

    /**
     * Finalise le mouvement d'une pièce lorsque le bouton de la souris est
     * relâché.
     */
    private void finalizeMove() {
        if (activeP != null) {
            if (validSquare) {
                confirmMove(); // Confirme et exécute le mouvement si valide
            } else {
                resetMove(); // Réinitialise tout si le mouvement n'est pas valide
            }
        }
    }

    /**
     * Confirme et exécute le mouvement d'une pièce active.
     */
    private void confirmMove() {
        copyPieces(simPieces, pieces);
        activeP.updatePosition();

        if (castlingP != null) {
            castlingP.updatePosition();
        }

        checkGameStatus();
    }

    /**
     * Vérifie l'état du jeu après un mouvement : échec, échec et mat ou
     * promotion.
     */
    private void checkGameStatus() {
        if (isKingInCheck() && isCheckmate()) {
            gameOver = true;
        } else if (isStalemate() && !isKingInCheck()) {
            stalemate = true;
        } else {
            handlePromotionOrChangePlayer();
        }
    }

    /**
     * Gère la promotion d'un pion ou change de joueur après un mouvement
     * valide.
     */
    private void handlePromotionOrChangePlayer() {
        if (canPromote()) {
            promotion = true;
        } else {
            changePlayer();
        }
    }

    /**
     * Réinitialise tous les éléments si un mouvement n'est pas valide.
     */
    private void resetMove() {
        copyPieces(pieces, simPieces);
        activeP.resetPosition();
        activeP = null;
    }

    /**
     * Simule un mouvement pour vérifier sa validité sans l'appliquer
     * réellement.
     */
    private void simulate() {
        validSquare = false;

        copyPieces(pieces, simPieces);

        resetCastlingPosition();

        updateActivePiecePosition();

        checkIfPieceCanMove();
    }

    /**
     * Réinitialise la position d'une pièce impliquée dans un roque.
     */
    private void resetCastlingPosition() {
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
    }

    /**
     * Met à jour la position de la pièce active en fonction de l'emplacement de
     * la souris.
     */
    private void updateActivePiecePosition() {
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;

        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);
    }

    /**
     * Vérifie si la pièce active peut se déplacer vers sa nouvelle position.
     */
    private void checkIfPieceCanMove() {
        if (activeP.canMove(activeP.col, activeP.row)) {

            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkCastling();

            if (!isIllegal(activeP) && !opponentCanCaptureKing()) {
                validSquare = true;
            }
        }
    }

    private boolean isIllegal(Piece king) {
        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing() {
        Piece king = getKing(false);

        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si un roi est en échec après un mouvement potentiel.
     */
    private boolean isKingInCheck() {
        Piece king = getKing(true);

        if (activeP.canMove(king.col, king.row)) {
            checkingP = activeP;
            return true;
        } else {
            checkingP = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent) {
        Piece king = null;

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }

    private boolean isCheckmate() {
        Piece king = getKing(true);

        if (kingCanMove(king)) {
            return false;
        } else {
            // check if you can block the attack with your piece

            // check the position of the attacking piece
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if (colDiff == 0) {
                // the checking piece is attacking vertically
                if (checkingP.row < king.row) {
                    // the checking piece is above the king
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    // the checking piece is below the king
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (rowDiff == 0) {
                // the checking piece is attacking horizontally
                if (checkingP.col < king.col) {
                    // the checking piece is to the left of the king
                    for (int col = checkingP.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col) {
                    // the checking piece is to the right of the king
                    for (int col = checkingP.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                // the checking piece is attacking diagonally
                if (checkingP.row < king.row) {
                    // the checking piece is above the king
                    if (checkingP.col < king.col) {
                        // the checking piece is in the upper left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        // the checking piece is in the upper right
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    // the checking piece is below the king
                    if (checkingP.col < king.col) {
                        // the checking piece is in the lower left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        // the checking piece is in the lower right
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean kingCanMove(Piece king) {
        int[][] directions = {
            {-1, -1}, {0, -1}, {1, -1},
            {-1, 0}, {1, 0},
            {-1, 1}, {0, 1}, {1, 1}
        };

        for (int[] direction : directions) {
            if (isValidMove(king, direction[0], direction[1])) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus) {
        boolean isValidMove = false;

        // Update the king's position for a second
        king.col += colPlus;
        king.row += rowPlus;

        if (king.canMove(king.col, king.row)) {
            if (king.hittingP != null) {
                simPieces.remove(king.hittingP.getIndex());
            }
            if (!isIllegal(king)) {
                isValidMove = true;
            }
        }
        // Reset the king's position and restore the removed piece
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private boolean isStalemate() {
        int count = 0;
        // Count the number of pieces 
        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                count++;
            }
        }

        // If only the king is left, the game is stalemate
        if (count == 1) {
            if (!kingCanMove(getKing(true))) {
                return true;
            }
        }

        return false;
    }

    private void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) {
                castlingP.col += 3;
            } else if (castlingP.col == 7) {
                castlingP.col -= 2;
            }

            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
            // Reset black's two stepped status
            for (Piece p : simPieces) {
                if (p.color == BLACK) {
                    p.twoStepped = false;
                }
            }

        } else {
            currentColor = WHITE;
            // Reset white's two stepped status
            for (Piece p : simPieces) {
                if (p.color == WHITE) {
                    p.twoStepped = false;
                }
            }
        }

        activeP = null;
    }

    private boolean canPromote() {
        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Bishop(currentColor, 9, 3));
                promoPieces.add(new Knight(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }

        return false;
    }

    private void promoting() {
        if (mouse.pressed) {
            for (Piece p : promoPieces) {
                if (p.col == mouse.x / Board.SQUARE_SIZE && p.row == mouse.y / Board.SQUARE_SIZE) {
                    switch (p.type) {
                        case ROOK ->
                            simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
                        case KNIGHT ->
                            simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
                        case BISHOP ->
                            simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                        case QUEEN ->
                            simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
                        default -> {
                        }
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        board.draw(g2);

        for (Piece p : simPieces) {
            p.draw(g2);
        }

        drawActivePiece(g2);

        drawStatusMessages(g2);
    }

    /**
     * Dessine la pièce actuellement sélectionnée par l'utilisateur et met en
     * surbrillance sa zone valide.
     *
     * @param g2 L'objet Graphics2D utilisé pour dessiner sur le panneau.
     */
    private void drawActivePiece(Graphics2D g2) {
        if (activeP != null) {
            highlightValidSquare(g2);

            activeP.draw(g2);
        }
    }

    /**
     * Met en surbrillance la case où se trouve actuellement la pièce active et
     * affiche une couleur différente selon sa validité.
     *
     * @param g2 L'objet Graphics2D utilisé pour dessiner sur le panneau.
     */
    private void highlightValidSquare(Graphics2D g2) {
        Color highlightColor = isIllegal(activeP) || opponentCanCaptureKing() ? Color.red : Color.white;

        g2.setColor(highlightColor);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawStatusMessages(Graphics2D g2) {
        // Status messages
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Consolas", Font.PLAIN, 30));
        g2.setColor(Color.white);

        if (promotion) {
            g2.drawString("Promotion :", 840, 150);
            for (Piece p : promoPieces) {
                g2.drawImage(p.image, p.getX(p.col), p.getY(p.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {
            if (currentColor == WHITE) {
                g2.drawString("Tour : Blancs", 840, 550);
                if (checkingP != null && checkingP.color == BLACK) {
                    g2.setColor(Color.red);
                    g2.drawString("Le Roi", 840, 650);
                    g2.drawString("est en Echec", 840, 700);
                }
            } else {
                g2.drawString("Tour : Noirs", 840, 250);
                if (checkingP != null && checkingP.color == WHITE) {
                    g2.setColor(Color.red);
                    g2.drawString("Le Roi", 840, 100);
                    g2.drawString("est en Echec", 840, 150);
                }
            }
        }

        // Game Over
        if (gameOver) {
            String s;
            if (currentColor == WHITE) {
                s = "Les Blancs ont gagnés !";
            } else {
                s = "Les Noirs ont gagnés !";
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.green);
            g2.drawString(s, 50, 420);
        }

        if (stalemate) {

            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.lightGray);
            g2.drawString("Match Nul", 200, 420);
        }

    }
}
