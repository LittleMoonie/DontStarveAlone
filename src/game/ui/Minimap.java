package src.game.ui;

import src.game.constants.GameConstants;
import src.game.entities.Player;
import src.game.world.Tile;
import src.game.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Minimap {
    private static final int TILE_SIZE = GameConstants.TILE_SIZE;
    private World world;
    private Player player;
    public int width = 150;
    public int height = 150;
    private float scale = 1;
    private BufferedImage minimapImage;
    private long lastUpdateTime = 0;
    private long updateInterval = 500; // Update every second

    private boolean isExpanded  = false; // Whether the minimap is open or not

    public Minimap(World world, Player player) {
        this.world = world;
        this.player = player;
        minimapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        redrawMinimap();
    }

    public void toggleExpanded() {
        isExpanded = !isExpanded;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > updateInterval) {
            redrawMinimap();
            lastUpdateTime = currentTime;
        }
    }

    private void redrawMinimap() {
        Graphics2D g = minimapImage.createGraphics();

        int worldX = (int) (player.x - (width / 2) * scale * TILE_SIZE);
        int worldY = (int) (player.y - (height / 2) * scale * TILE_SIZE);

        // Clear previous image
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int tileX = (int) (worldX + x * scale * TILE_SIZE);
                int tileY = (int) (worldY + y * scale * TILE_SIZE);

                Tile tile = world.getTileAt(tileX, tileY, true); // Non-blocking

                if (tile != null && !tile.type.equals("loading")) {
                    g.setColor(getColorForTile(tile));
                } else {
                    // Use a default color or skip rendering
                    g.setColor(Color.BLACK);
                }
                g.fillRect(x, y, 1, 1);
            }
        }

        g.dispose();
    }


    public void draw(Graphics2D g2) {
//        int minimapX = 20;
//        int minimapY = 20;
//
//        // Draw cached minimap image
//        g2.drawImage(minimapImage, minimapX, minimapY, null);
//
//        // Draw player position
//        g2.setColor(Color.RED);
//        g2.fillRect(minimapX + width / 2, minimapY + height / 2, 5, 5);
//
//        // Draw border
//        g2.setColor(Color.BLACK);
//        g2.drawRect(minimapX, minimapY, width, height);


        int minimapX, minimapY;

        if (isExpanded) {
            // Positionne la minimap agrandie au centre de l'écran
            minimapX = (1920 - width * 4) / 2; // Largeur x4 pour l'agrandir
            minimapY = (1080 - height * 4) / 2;

            // Dessine l'image de la minimap agrandie
            g2.drawImage(minimapImage, minimapX, minimapY, width * 4, height * 4, null);

            // Centre le joueur sur la minimap agrandie
            int playerCenterX = minimapX + (width * 4) / 2;
            int playerCenterY = minimapY + (height * 4) / 2;

            g2.setColor(Color.RED);
            g2.fillRect(playerCenterX - 2, playerCenterY - 2, 5, 5);
        } else {
            // Positionne la minimap dans un coin pour la version réduite
            minimapX = 20;
            minimapY = 20;

            g2.drawImage(minimapImage, minimapX, minimapY, null);

            // Position du joueur au centre de la minimap réduite
            g2.setColor(Color.RED);
            g2.fillRect(minimapX + width / 2 - 2, minimapY + height / 2 - 2, 5, 5);
        }

        // Bordure de la minimap
        g2.setColor(Color.BLACK);
        if (isExpanded) {
            g2.drawRect(minimapX, minimapY, width * 4, height * 4);
        } else {
            g2.drawRect(minimapX, minimapY, width, height);
        }

    }

    private Color getColorForTile(Tile tile) {
        if (tile == null) {
            return Color.BLACK;
        }
        switch (tile.type) {
            case "grass":
                return new Color(34, 139, 34);
            case "sand":
                return new Color(194, 178, 128);
            case "water":
                return new Color(65, 105, 225);
            case "forest":
                return new Color(0, 100, 0);
            default:
                return Color.BLACK;
        }
    }
    public BufferedImage getMinimapImage() {
        return minimapImage;
    }

}
