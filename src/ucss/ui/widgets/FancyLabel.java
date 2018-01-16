/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author John
 */
public class FancyLabel extends ScrollPane {
    private final VBox box = new VBox();
    public List<FancyNode> list = new ArrayList<>();
    private SimpleStringProperty linker = new SimpleStringProperty();
    
    public FancyLabel() { 
        init();
    }
    
    public FancyLabel(String text) {
        setText(text);
        init();
    }
    
    public FancyLabel(InputStream is) {
        
        try {
            setFromStream(is);
        } catch (IOException ex) {
            setText("ERROR LOADING FROM STREAM");
        }
        init();
    }
    
    public final void setFromStream(InputStream is) throws IOException {
        if (is == null) {
            setText("NULL STREAM");
            return;
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
        String line = buf.readLine(); 
        StringBuilder sb = new StringBuilder(); 
        while(line != null){ 
            sb.append(line).append(" "); 
            line = buf.readLine(); 
        }
        setText(sb.toString());
    }
    
    private void init() {
        setContent(box);
        this.setFitToWidth(true);
        this.setStyle("-fx-background-color:#ddd; -fx-background:#fff;");
        box.setFillWidth(true);
        box.setSpacing(4);
        box.setStyle("-fx-padding:1.0em 3em;-fx-background-color:white;");
    }
    
    public void setText(String text) {
        parse(text);
        update();
    }
    
    private void parse(String t) {
        //if (c == '\b' || c == '\f' || c == '\n' || c == '\r' || c == '\t' || c == '\\') {
            
        list.clear();
        boolean wait = false;
        
        String value = "";
        t = t.replaceAll("\n", " ");
        t = t.replaceAll("  ", " ");
        t = t.replaceAll("> ", ">");
        for(char c: t.toCharArray()) {
            if (c == '\b' || c == '\f' || c == '\n' || c == '\r' || c == '\t' || c == '\\') {
                if (c == '\t') value = c + value;
                System.out.println("VAL = " + value);
                if (!value.isEmpty()) {
                    writeValue(value, false);
                    value = "";
                }
                continue;
            }

            if (wait) {
                if (c == '>') {
                    wait = false;
                    writeValue(value, true);
                    value = "";
                    continue;
                }
                //value+=c;
            }
            if (c == '<') {
                wait = true;
                if (!value.isEmpty()) {
                    writeValue(value, false);
                    value = "";
                }
            } else {
                value+=c;
            }
        }
        
        if (!value.isEmpty()) {
            writeValue(value, false);
            value = "";
        }
        
    }

    private void writeValue(String t, boolean b) {
        list.add(new FancyNode(t, b));
    }

    private boolean 
            isBold          = false,
            isItalic        = false,
            isUnderlined    = false,
            isStrikeThrough = false;

    private FontWeight fontWeight = FontWeight.NORMAL;
    private FontPosture fontPosture = FontPosture.REGULAR;
        
    private boolean newLine = false;
        
    private double 
            baseSize = 16,
            fontSize = 16;
    
    private TextFlow textFlow = null;
    private TextFlow prevFlow = null;
    private TextFlow target = textFlow;
    private Color textColor = Color.BLACK; 
    private boolean imageTag = false;
    private void update() {

        box.getChildren().clear();
        
        isBold          = false;
        isItalic        = false;
        isUnderlined    = false;
        isStrikeThrough = false;
    
        box.getChildren().clear();
        
        isBold          = false;
        isItalic        = false;
        isUnderlined    = false;
        isStrikeThrough = false;
        
        fontWeight = FontWeight.NORMAL;
        fontPosture = FontPosture.REGULAR;
        
        newLine = false;
        
        baseSize = 16;
        fontSize = 16;
        
        TextAlignment alignment = TextAlignment.LEFT;
        
        textFlow = tFlow();
        prevFlow = null;
        target = textFlow;
        textColor = Color.BLACK;
        double oldSize = fontSize;
        FontWeight oldWeight = fontWeight;
        boolean wait = false;
        
        imageTag = false;
        
        for(FancyNode n: list) {
            
            if (n.isTag) {
                if (n.value.toLowerCase().startsWith("ref:")) {
                    String[] p = n.value.substring(4).split("=");
                    if (p.length == 2) {
                        addLink(p[0], p[1]);
                    }
                    
                    continue;
                }
                newLine = false;
                switch (n.value.toLowerCase()) {
                    case "image":
                        imageTag = !n.isCloseTag;
                        break;
                    case "^":
                        if (!n.isCloseTag) {
                            oldSize = fontSize;
                            oldWeight = fontWeight;
                            fontSize*=1.5;
                            fontWeight = FontWeight.BOLD;
                            wait = true;
                        } else {
                            fontSize = oldSize;
                            fontWeight = oldWeight;  
                            wait = false;
                        } 
                        
                        break;
                    case "b": 
                    case "bold":
                        fontWeight = (!n.isCloseTag?fontWeight.BOLD:fontWeight.NORMAL); 
                        break;
                    case "i": 
                    case "italic":
                        fontPosture = (n.isCloseTag?FontPosture.REGULAR:FontPosture.ITALIC);
                        break;
                    case "s": 
                    case "strikethrough":
                        isStrikeThrough = !n.isCloseTag; 
                        break;
                    case "u": 
                    case "underline":
                        isUnderlined = !n.isCloseTag; 
                        break;
                    case "h1":
                        if (!n.isCloseTag && target.getTextAlignment() != TextAlignment.CENTER)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.CENTER);
                        fontWeight = (!n.isCloseTag?fontWeight.BOLD:fontWeight.NORMAL); 
                        fontSize = (!n.isCloseTag?baseSize*2:baseSize);
                        newLine = n.isCloseTag;
                        break;
                    case "h2":
                        if (target.getTextAlignment() != TextAlignment.CENTER)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.CENTER);
                        fontWeight = (!n.isCloseTag?fontWeight.BOLD:fontWeight.NORMAL); 
                        fontSize = (!n.isCloseTag?baseSize*1.667:baseSize);
                        newLine = n.isCloseTag;
                        break;
                    case "h3":
                        if (target.getTextAlignment() != TextAlignment.CENTER)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.CENTER);
                        fontWeight = (!n.isCloseTag?fontWeight.BOLD:fontWeight.NORMAL); 
                        fontSize = (!n.isCloseTag?baseSize*1.33:baseSize);
                        newLine = n.isCloseTag;
                        break;
                    case "h4":
                        if (target.getTextAlignment() != TextAlignment.CENTER)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.CENTER);
                        fontWeight = (!n.isCloseTag?fontWeight.BOLD:fontWeight.NORMAL); 
                        fontSize = baseSize;
                        newLine = n.isCloseTag;
                        break;
                    case "h5":
                        if (target.getTextAlignment() != TextAlignment.CENTER)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.CENTER);
                        fontWeight = (!n.isCloseTag?fontWeight.BOLD:fontWeight.NORMAL); 
                        fontSize = (!n.isCloseTag?baseSize*0.875:baseSize);
                        newLine = n.isCloseTag;
                        break;
                    case "h6":
                        if (target.getTextAlignment() != TextAlignment.CENTER)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.CENTER);
                        fontWeight = (!n.isCloseTag?fontWeight.BOLD:fontWeight.NORMAL); 
                        fontSize = (!n.isCloseTag?baseSize*0.75:baseSize);
                        newLine = n.isCloseTag;
                        break;
                    case "div":
                        if (!n.isCloseTag) {  
                            target = tFlow();  
                        } 
                        break;
                    case "quote":
                        if (!n.isCloseTag) {  
                            target = tFlow();  
                            target.setStyle("-fx-background-color:#fff;-fx-padding:1em 3em 1em 3em;");
                            target.setLineSpacing(2.0);
                            fontPosture = FontPosture.ITALIC;
                            
                            fontSize *= 0.875;
                        } 
                        break;
                    case "high":
                        if (n.isCloseTag)
                            textColor = Color.BLACK;
                        else
                            textColor = Color.rgb(0, 128, 255);
                        break;
                    case "ul":
                        if (!n.isCloseTag) {
                            target = tFlow();
                            fontSize = baseSize * 1.33;
                            fontPosture = FontPosture.REGULAR;
                            alignment = TextAlignment.LEFT;
                        }
                        
                        break;
                    case "l3":
                        if (!n.isCloseTag) {
                            Text T = new Text("\u2219");
                            T.setTranslateY(-8);
                            T.setFont(Font.font(20.0));
                            HBox hb = new HBox(T);
                            hb.setPadding(new Insets(0, 0, 0, 92));
                            hb.setAlignment(Pos.CENTER_LEFT);
                            target = tFlow(hb);  
                            target.setPadding(new Insets(0, 0, 0, 18));
                            fontSize = baseSize * 0.875;
                            target.setLineSpacing(2.0);
                            fontPosture = FontPosture.ITALIC;
                            HBox.setHgrow(target, Priority.ALWAYS);
                            box.getChildren().add(hb);
                        } else {
                           // target = tFlow();
                        }
                        break;
                    case "l2":
                        if (!n.isCloseTag) {
                            Text T = new Text("\u25E6");
                            T.setTranslateY(-6);
                            T.setFont(Font.font(20.0));
                            HBox hb = new HBox(T);
                            hb.setPadding(new Insets(0, 0, 0, 72));
                            hb.setAlignment(Pos.CENTER_LEFT);
                            target = tFlow(hb);  
                            target.setPadding(new Insets(0, 0, 0, 18));
                            fontSize = baseSize;
                            fontPosture = FontPosture.REGULAR;
                            target.setLineSpacing(2.0);
                            HBox.setHgrow(target, Priority.ALWAYS);
                            box.getChildren().add(hb);
                        } else {
                           // target = tFlow();
                        }
                        break;
                    case "l1":
                        if (!n.isCloseTag) {
                            Text T = new Text("\u25CF");
                            T.setTranslateY(-4);
                            T.setFont(Font.font(20.0));
                            HBox hb = new HBox(T);
                            hb.setPadding(new Insets(0, 0, 0, 36));
                            hb.setAlignment(Pos.CENTER_LEFT);
                            target = tFlow(hb);  
                            target.setPadding(new Insets(0, 0, 0, 18));
                            fontSize = baseSize * 1.25;
                            fontPosture = FontPosture.REGULAR;
                            target.setLineSpacing(2.0);
                            HBox.setHgrow(target, Priority.ALWAYS);
                            box.getChildren().add(hb);
                        } else {
                           // target = tFlow();
                        }
                        
                        break;
                    case "bar":
                        Separator s = new Separator();
                        box.getChildren().add(s);
                    case "newline":
                        target.getChildren().add(new Text("\n"));
                        break;
                    case "br":
                        target = tFlow();  
                        break;
                    case "left":
                        if (target.getTextAlignment() != TextAlignment.LEFT && !wait)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.LEFT);
                        break;
                    case "right":
                        if (target.getTextAlignment() != TextAlignment.RIGHT && !wait)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.RIGHT);
                        break;
                    case "justify":
                        if (target.getTextAlignment() != TextAlignment.JUSTIFY && !wait)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.JUSTIFY);
                        break;
                    case "center":
                        if (target.getTextAlignment() != TextAlignment.CENTER && !wait)
                            target = tFlow();
                        target.setTextAlignment(TextAlignment.CENTER);
                        break;
                    case "12":
                        fontSize = (n.isCloseTag)?baseSize:12;
                        break;
                     case "14":
                        fontSize = (n.isCloseTag)?baseSize:14;
                        break;
                    case "16":
                        fontSize = (n.isCloseTag)?baseSize:16;
                        break;
                    case "20":
                        fontSize = (n.isCloseTag)?baseSize:20;
                        break;
                    case "24":
                        fontSize = (n.isCloseTag)?baseSize:24;
                        break;
                    case "default":
                        fontSize = baseSize;
                        break;
                    
                }
                wait = true;
            } else {
                commit(n);
                wait = false;
            }
        }
        
        //t.setTextAlignment(TextAlignment.LEFT);
        
    }
    
    private TextFlow tFlow() {
        TextFlow t = new TextFlow();
        t.setLineSpacing(6.0);
        t.setStyle("-fx-background-color:#fff;-fx-padding:0.75em 0em 0.75em 0em;");
        box.getChildren().add(t);
        return t;
    }
    
    private TextFlow tFlow(Pane in) {
        TextFlow t = new TextFlow();
        t.setLineSpacing(6.0);
        t.setStyle("-fx-background-color:#fff;-fx-padding:0em 0em 0em 0em;");
        t.setStyle("-fx-background-color:#fff;");
        in.getChildren().add(t);
        return t;
    }

    private void commit(FancyNode n) {
        
        if (n.value.isEmpty())
            return;
        
        if (!imageTag) {

            String v = n.value;
            if (newLine)
                v="\n" + v;

            Text t = new Text(v);
            t.setFill(textColor);
            t.setFont(Font.font("san-serif", fontWeight, fontPosture, fontSize));
            target.getChildren().add(t);
        } else {
            try {
                String xValue[] = n.value.split("scale=");
                double scale = xValue.length==1?1.0:getDouble(xValue[1]);
                Image img = new Image(getClass().getResourceAsStream("/resources/" + xValue[0].trim()));
                ImageView imv = new ImageView(img);
                imv.setPreserveRatio(true);
                imv.setFitHeight(scale * img.getHeight());
                //imv.setScaleY(scale);
                
                target.getChildren().add(imv);
            } catch (Exception e) {}
        }
        newLine = false;
    }

    private double getDouble(String v) {
        double result = 1.0;
        
        try {
            result = Double.valueOf(v);
        } catch (Exception e) {}
        
        return result;
    }
    
    private void addLink(String text, String name) {
        Text t = new Text(text);
        t.setId(name);
        t.setOnMouseClicked( e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                String n = ((Node)e.getSource()).getId();
                linker.set(n);
                linker.set("");
            }
        });
       
        t.setFill(Color.BLUE);
        t.setCursor(Cursor.HAND);
        t.setFont(Font.font("san-serif", fontWeight, fontPosture, fontSize));
        target.getChildren().add(t);
        
    }
    
    private class FancyNode {
        
        public final String value;
        public final boolean isTag;
        public final boolean isCloseTag;
        
        FancyNode(String v, boolean t) {
            v = v.replaceAll("\n", "");
            if (t) {
                isCloseTag = v.contains("/");
                v = v.replaceAll("/", "");
            } else
                isCloseTag = false;
        
            value = v;
            isTag = t;
            
            
        }
        
    }
    
    public final void setOnLinkSelected(ChangeListener<String> listener) {
        linker.addListener(listener);
    }
}