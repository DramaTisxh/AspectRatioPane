package com.github.dramatisxh.aspectratiopane;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * AspectRatioPane allows the child nodes to keep their aspect ratio.
 */
public class AspectRatioPane extends Pane {

    private static final String ASPECT_RATIO = "aspectratiopane-ratio";
    private static final String ALIGNMENT = "aspectratiopane-alignment";

    /* ******************************************************************
     *  BEGIN static methods
     ********************************************************************/

    public static void setAspectRatio(Node child, Double value) {
        setConstraint(child, ASPECT_RATIO, value);
    }

    /**
     * Returns the child's aspect ratio if set.
     *
     * @param child the child node of an aspect ratio pane
     * @return the aspect ratio from the aspect ratio pane's child or null if no aspect ratio was set
     */
    public static Double getAspectRatio(Node child) {
        return (Double) getConstraint(child, ASPECT_RATIO);
    }

    /**
     * Sets the alignment for the child when contained by an aspect ratio pane.
     * If set, will override the aspect ratio pane's default alignment for the child's position.
     * Setting the value to null will remove the constraint.
     *
     * @param child the child node of an aspect ratio pane
     * @param value the alignment position for the child
     */
    public static void setAlignment(Node child, Pos value) {
        setConstraint(child, ALIGNMENT, value);
    }

    /**
     * Returns the child's alignment constraint if set.
     *
     * @param child the child node of an aspect ratio pane
     * @return the alignment position for the child or null if no alignment was set
     */
    public static Pos getAlignment(Node child) {
        return (Pos) getConstraint(child, ALIGNMENT);
    }

    /**
     * Removes all aspect ratio pane constraints from the child node.
     *
     * @param child the child node
     */
    public static void clearConstraints(Node child) {
        setAspectRatio(child, null);
        setAlignment(child, null);
    }

    /* ******************************************************************
     *  END static methods
     ********************************************************************/

    /**
     * Creates an AspectRatioPane layout.
     */
    public AspectRatioPane() {
        super();
    }

    /**
     * Creates an AspectRatioPane layout with the given children.
     *
     * @param children The initial set of children for this pane.
     * @since JavaFX 8.0
     */
    public AspectRatioPane(Node... children) {
        super();
        getChildren().addAll(children);
    }

    /**
     * Computes the width based on the given aspect ratio and height.
     *
     * @param aspectRatio the aspect ratio
     * @param height      the height
     * @return the computed width
     */
    private double computeWidth(Double aspectRatio, double height) {
        return height * aspectRatio;
    }

    /**
     * Computes the height based on the given aspect ratio and width.
     *
     * @param aspectRatio the aspect ratio
     * @param width the width
     * @return the computed height
     */
    private double computeHeight(Double aspectRatio, double width) {
        return width / aspectRatio;
    }

    /**
     * Computes the child's x position.
     *
     * @param hPos      the horizontal position
     * @param width     the child's width
     * @param paneWidth the pane's width
     * @return the x position
     */
    private double computeChildX(HPos hPos, double width, double paneWidth) {
        if (hPos.equals(HPos.CENTER)) {
            return (paneWidth - width) / 2;
        } else if (hPos.equals(HPos.RIGHT)) {
            return paneWidth - width;
        } else {
            return 0;
        }
    }

    /**
     * Computes the child's y position.
     *
     * @param vPos       the vertical position
     * @param height     the child's height
     * @param paneHeight the pane's height
     * @return the y position
     */
    private double computeChildY(VPos vPos, double height, double paneHeight) {
        if (vPos.equals(VPos.BOTTOM)) {
            return paneHeight - height;
        } else if (vPos.equals(VPos.CENTER) || vPos.equals(VPos.BASELINE)) {
            return (paneHeight - height) / 2;
        } else {
            return 0;
        }
    }

    @Override
    protected void layoutChildren() {
        final List<Node> children = getManagedChildren();
        final double paneWidth = this.getWidth();
        final double paneHeight = this.getHeight();
        final double paneAspectRatio = paneWidth / paneHeight;

        double x;
        double y;
        double width = paneWidth;
        double height = paneHeight;

        for (Node child : children) {
            Double aspectRatio = getAspectRatio(child);
            if (aspectRatio == null) {
                double prefWidth = child.prefWidth(-1);
                double prefHeight = child.prefHeight(-1);
                aspectRatio = prefWidth / prefHeight;
            }

            if (paneAspectRatio > aspectRatio) {
                width = computeWidth(aspectRatio, paneHeight);
            } else if (paneAspectRatio < aspectRatio) {
                height = computeHeight(aspectRatio, paneWidth);
            }

            Pos pos = getAlignment(child);
            if (pos == null) {
                pos = Pos.CENTER;
            }
            x = computeChildX(pos.getHpos(), width, paneWidth);
            y = computeChildY(pos.getVpos(), height, paneHeight);

            child.resizeRelocate(x, y, width, height);
        }
    }

    /* ******************************************************************
     *  BEGIN methods from javafx.scene.layout.pane class
     ********************************************************************/

    static void setConstraint(Node node, Object key, Object value) {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }
        if (node.getParent() != null) {
            node.getParent().requestLayout();
        }
    }

    static Object getConstraint(Node node, Object key) {
        if (node.hasProperties()) {
            return node.getProperties().get(key);
        }
        return null;
    }

    /* ******************************************************************
     *  END methods from javafx.scene.layout.pane class
     ********************************************************************/
}

