package de.loc.input.userinterface;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class LandsOfCinderWindow extends Table {
    private static final Vector2 tmpPosition = new Vector2();
    private static final Vector2 tmpSize = new Vector2();
    private static final int MOVE = 1 << 5;

    private WindowStyle style;
    private boolean isMovable = true, isModal, isResizable;
    private int resizeBorder = 8;
    private boolean dragging;
    private boolean keepWithinStage = true;
    private boolean drawTitleTable;

    public LandsOfCinderWindow(Skin skin) {
        this(skin.get(WindowStyle.class));
        this.setSkin(skin);
    }

    public LandsOfCinderWindow(Skin skin, String styleName) {
        this(skin.get(styleName, WindowStyle.class));
        this.setSkin(skin);
    }

    public LandsOfCinderWindow(WindowStyle style) {

        this.setTouchable(Touchable.enabled);
        this.setClip(true);

        this.setStyle(style);
        this.setWidth(150);
        this.setHeight(150);

        this.addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                LandsOfCinderWindow.this.toFront();
                return false;
            }
        });
        this.addListener(new InputListener() {
            int edge;
            float startX, startY, lastX, lastY;

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if ( button == 0 ) {
                    int border = LandsOfCinderWindow.this.resizeBorder;
                    float width = LandsOfCinderWindow.this.getWidth(), height = LandsOfCinderWindow.this.getHeight();
                    this.edge = 0;
                    if ( LandsOfCinderWindow.this.isResizable && x >= 0 && x < width && y >= 0 && y < height ) {
                        if ( x < border ) {
                            this.edge |= Align.left;
                        }
                        if ( x > width - border ) {
                            this.edge |= Align.right;
                        }
                        if ( y < border ) {
                            this.edge |= Align.bottom;
                        }
                        if ( y > height - border ) {
                            this.edge |= Align.top;
                        }
                        if ( this.edge != 0 ) {
                            border += 25;
                        }
                        if ( x < border ) {
                            this.edge |= Align.left;
                        }
                        if ( x > width - border ) {
                            this.edge |= Align.right;
                        }
                        if ( y < border ) {
                            this.edge |= Align.bottom;
                        }
                        if ( y > height - border ) {
                            this.edge |= Align.top;
                        }
                    }
                    if ( LandsOfCinderWindow.this.isMovable
                         && this.edge == 0
                         && y <= height
                         && y >= height - LandsOfCinderWindow.this.getPadTop()
                         && x >= 0
                         && x <= width ) {
                        this.edge = MOVE;
                    }
                    LandsOfCinderWindow.this.dragging = this.edge != 0;
                    this.startX = x;
                    this.startY = y;
                    this.lastX = x;
                    this.lastY = y;
                }
                return this.edge != 0 || LandsOfCinderWindow.this.isModal;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                LandsOfCinderWindow.this.dragging = false;
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if ( !LandsOfCinderWindow.this.dragging ) {
                    return;
                }
                float width = LandsOfCinderWindow.this.getWidth(), height = LandsOfCinderWindow.this.getHeight();
                float windowX = LandsOfCinderWindow.this.getX(), windowY = LandsOfCinderWindow.this.getY();

                float minWidth = LandsOfCinderWindow.this.getMinWidth(), maxWidth = LandsOfCinderWindow.this.getMaxWidth();
                float minHeight = LandsOfCinderWindow.this.getMinHeight(), maxHeight = LandsOfCinderWindow.this.getMaxHeight();
                Stage stage = LandsOfCinderWindow.this.getStage();
                boolean clampPosition = LandsOfCinderWindow.this.keepWithinStage && LandsOfCinderWindow.this.getParent() == stage.getRoot();

                if ( (this.edge & MOVE) != 0 ) {
                    float amountX = x - this.startX, amountY = y - this.startY;
                    windowX += amountX;
                    windowY += amountY;
                }
                if ( (this.edge & Align.left) != 0 ) {
                    float amountX = x - this.startX;
                    if ( width - amountX < minWidth ) {
                        amountX = -(minWidth - width);
                    }
                    if ( clampPosition && windowX + amountX < 0 ) {
                        amountX = -windowX;
                    }
                    width -= amountX;
                    windowX += amountX;
                }
                if ( (this.edge & Align.bottom) != 0 ) {
                    float amountY = y - this.startY;
                    if ( height - amountY < minHeight ) {
                        amountY = -(minHeight - height);
                    }
                    if ( clampPosition && windowY + amountY < 0 ) {
                        amountY = -windowY;
                    }
                    height -= amountY;
                    windowY += amountY;
                }
                if ( (this.edge & Align.right) != 0 ) {
                    float amountX = x - this.lastX;
                    if ( width + amountX < minWidth ) {
                        amountX = minWidth - width;
                    }
                    if ( clampPosition && windowX + width + amountX > stage.getWidth() ) {
                        amountX = stage.getWidth() - windowX - width;
                    }
                    width += amountX;
                }
                if ( (this.edge & Align.top) != 0 ) {
                    float amountY = y - this.lastY;
                    if ( height + amountY < minHeight ) {
                        amountY = minHeight - height;
                    }
                    if ( clampPosition && windowY + height + amountY > stage.getHeight() ) {
                        amountY = stage.getHeight() - windowY - height;
                    }
                    height += amountY;
                }
                this.lastX = x;
                this.lastY = y;
                LandsOfCinderWindow.this.setBounds(Math.round(windowX), Math.round(windowY), Math.round(width), Math.round(height));
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                return LandsOfCinderWindow.this.isModal;
            }

            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                return LandsOfCinderWindow.this.isModal;
            }

            public boolean keyDown(InputEvent event, int keycode) {
                return LandsOfCinderWindow.this.isModal;
            }

            public boolean keyUp(InputEvent event, int keycode) {
                return LandsOfCinderWindow.this.isModal;
            }

            public boolean keyTyped(InputEvent event, char character) {
                return LandsOfCinderWindow.this.isModal;
            }
        });
    }

    public void setStyle(WindowStyle style) {
        if ( style == null ) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        this.setBackground(style.background);
        this.invalidateHierarchy();
    }

    /**
     * Returns the window's style. Modifying the returned style may not have an effect until {@link #setStyle(WindowStyle)} is
     * called.
     */
    public WindowStyle getStyle() {
        return this.style;
    }

    void keepWithinStage() {
        if ( !this.keepWithinStage ) {
            return;
        }
        Stage stage = this.getStage();
        Camera camera = stage.getCamera();
        if ( camera instanceof OrthographicCamera ) {
            OrthographicCamera orthographicCamera = (OrthographicCamera) camera;
            float parentWidth = stage.getWidth();
            float parentHeight = stage.getHeight();
            if ( this.getX(Align.right) - camera.position.x > parentWidth / 2 / orthographicCamera.zoom ) {
                this.setPosition(camera.position.x + parentWidth / 2 / orthographicCamera.zoom, this.getY(Align.right), Align.right);
            }
            if ( this.getX(Align.left) - camera.position.x < -parentWidth / 2 / orthographicCamera.zoom ) {
                this.setPosition(camera.position.x - parentWidth / 2 / orthographicCamera.zoom, this.getY(Align.left), Align.left);
            }
            if ( this.getY(Align.top) - camera.position.y > parentHeight / 2 / orthographicCamera.zoom ) {
                this.setPosition(this.getX(Align.top), camera.position.y + parentHeight / 2 / orthographicCamera.zoom, Align.top);
            }
            if ( this.getY(Align.bottom) - camera.position.y < -parentHeight / 2 / orthographicCamera.zoom ) {
                this.setPosition(this.getX(Align.bottom), camera.position.y - parentHeight / 2 / orthographicCamera.zoom, Align.bottom);
            }
        } else if ( this.getParent() == stage.getRoot() ) {
            float parentWidth = stage.getWidth();
            float parentHeight = stage.getHeight();
            if ( this.getX() < 0 ) {
                this.setX(0);
            }
            if ( this.getRight() > parentWidth ) {
                this.setX(parentWidth - this.getWidth());
            }
            if ( this.getY() < 0 ) {
                this.setY(0);
            }
            if ( this.getTop() > parentHeight ) {
                this.setY(parentHeight - this.getHeight());
            }
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        Stage stage = this.getStage();
        if ( stage.getKeyboardFocus() == null ) {
            stage.setKeyboardFocus(this);
        }

        this.keepWithinStage();

        if ( this.style.stageBackground != null ) {
            this.stageToLocalCoordinates(tmpPosition.set(0, 0));
            this.stageToLocalCoordinates(tmpSize.set(stage.getWidth(), stage.getHeight()));
            this.drawStageBackground(
                batch,
                parentAlpha,
                this.getX() + tmpPosition.x,
                this.getY() + tmpPosition.y,
                this.getX() + tmpSize.x,
                this.getY() + tmpSize.y);
        }

        super.draw(batch, parentAlpha);
    }

    protected void drawStageBackground(Batch batch, float parentAlpha, float x, float y, float width, float height) {
        Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        this.style.stageBackground.draw(batch, x, y, width, height);
    }

    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        super.drawBackground(batch, parentAlpha, x, y);

        // Manually draw the title table before clipping is done.

        float padTop = this.getPadTop(), padLeft = this.getPadLeft();

        this.drawTitleTable = true;

        this.drawTitleTable = false; // Avoid drawing the title table again in drawChildren.
    }

    public Actor hit(float x, float y, boolean touchable) {
        Actor hit = super.hit(x, y, touchable);
        if ( hit == null && this.isModal && (!touchable || this.getTouchable() == Touchable.enabled) ) {
            return this;
        }
        float height = this.getHeight();
        if ( hit == null || hit == this ) {
            return hit;
        }
        if ( y <= height && y >= height - this.getPadTop() && x >= 0 && x <= this.getWidth() ) {
            // Hit the title bar, don't use the hit child if it is in the Window's table.
            Actor current = hit;
            while ( current.getParent() != this ) {
                current = current.getParent();
            }
            if ( this.getCell(current) != null ) {
                return this;
            }
        }
        return hit;
    }

    public boolean isMovable() {
        return this.isMovable;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    public boolean isModal() {
        return this.isModal;
    }

    public void setModal(boolean isModal) {
        this.isModal = isModal;
    }

    public void setKeepWithinStage(boolean keepWithinStage) {
        this.keepWithinStage = keepWithinStage;
    }

    public boolean isResizable() {
        return this.isResizable;
    }

    public void setResizable(boolean isResizable) {
        this.isResizable = isResizable;
    }

    public void setResizeBorder(int resizeBorder) {
        this.resizeBorder = resizeBorder;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    /**
     * The style for a window, see {@link Window}.
     *
     * @author Philipp Schönhuth
     */
    public static class WindowStyle {
        /**
         * Optional.
         */
        public Drawable background;
        public BitmapFont titleFont;
        /**
         * Optional.
         */
        public Color titleFontColor = new Color(1, 1, 1, 1);
        /**
         * Optional.
         */
        public Drawable stageBackground;

        public WindowStyle() {
        }

        public WindowStyle(BitmapFont titleFont, Color titleFontColor, Drawable background) {
            this.background = background;
            this.titleFont = titleFont;
            this.titleFontColor.set(titleFontColor);
        }

        public WindowStyle(WindowStyle style) {
            this.background = style.background;
            this.titleFont = style.titleFont;
            this.titleFontColor = new Color(style.titleFontColor);
        }
    }

}