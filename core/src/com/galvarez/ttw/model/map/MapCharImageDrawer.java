package com.galvarez.ttw.model.map;

import java.util.Properties;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * @author hundun
 * Created on 2021/11/03
 */
public class MapCharImageDrawer {
    
    Terrain[][] terrains;
    int width;
    int height;
    
    public MapCharImageDrawer() {
        
        MapGenerator mapGenerator = new HexMapTerraGenerator();
        Properties props = properties(4, 1, 1);
        this.terrains = mapGenerator.getMapData(props);
        this.width = terrains[0].length;
        this.height = terrains.length;
        
    }
    
    public static void main(String[] args) {
        
        MapCharImageDrawer drawer = new MapCharImageDrawer();
        drawer.draw();
    }
    
    public Properties properties(int noise, int width, int height) {
        Properties props = new Properties();
        props.setProperty("noise", String.valueOf(noise));
        props.setProperty("width", String.valueOf(width));
        props.setProperty("height", String.valueOf(height));
        return props;
      }
    
    public void draw() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < width; i++) {
            appendOneHexTop(builder);
        }
        builder.append("\n");
        for (int i = 0; i < height; i++) {
            if (i % 2 == 1) {
                builder.append("  ");
            }
            for (int j = 0; j < width; j++) {
                appendOneHexMiddle(builder, terrains[i][j]);
            }
            builder.append("\n");
            if (i % 2 == 1) {
                builder.append("  ");
            }
            for (int j = 0; j < width; j++) {
                appendOneHexBottom(builder);
            }
            builder.append("\n");
        }
        System.out.println(builder.toString());
    }
    
    private void appendOneHexTop(StringBuilder builder) {
        builder.append(" / \\");
    }
    
    private void appendOneHexBottom(StringBuilder builder) {
        builder.append(" \\ /");
    }
    
    private void appendOneHexMiddle(StringBuilder builder, Terrain terrain) {
        builder.append("| " + terrain.ordinal() + " ");
    }
    
    
}
