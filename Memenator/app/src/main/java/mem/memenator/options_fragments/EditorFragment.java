package mem.memenator.options_fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import mem.memenator.MainActivity;
import mem.memenator.R;
import mem.memenator.model.EditActionType;


/**
 * Fragment for editing images, publishing them, opens after selecting
 * camera and taking picture, choosing image to edit (gallery, samples)
 * or just choosing option edit, everything from right navigation drawer
 */
public class EditorFragment extends Fragment implements View.OnTouchListener {
    public EditorFragment() {
    }

    private Bitmap copy;
    private View rootView;
    public static EditActionType Action;
    private float startX;
    private float startY;
    public static int COLOR = Color.BLACK;
    private static EditText editText;
    private TextView description;
    private TextView labelWriteText;
    private Button changeColorButton;
    private Button changeFontButton;
    private static TextView selectedColor;
    private static final int PENSIZE = 5;
    public static String FONT_STYLE = "Helvetica";
    public static int FONT_ADDITIONAL_STYLE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        Action = EditActionType.FilledRectangle;
        rootView = inflater.inflate(R.layout.editor_fragment, container, false);
        editText = (EditText) rootView.findViewById(R.id.textToPasteIntoEditedImage);
        description = (TextView) rootView.findViewById(R.id.editorDescription);
        labelWriteText = (TextView) rootView.findViewById(R.id.labelWriteText);
        changeColorButton = (Button) rootView.findViewById(R.id.changeColorButton);
        changeFontButton = (Button) rootView.findViewById(R.id.changeFontButton);
        selectedColor = (TextView) rootView.findViewById(R.id.selectedColor);
        selectedColor.setBackgroundColor(COLOR);
        editText.setTextColor(COLOR);
        description.setText(getResources().getString(R.string.filledRectangleOption));
        editText.setVisibility(View.GONE);
        labelWriteText.setVisibility(View.GONE);
        changeColorButton.setVisibility(View.VISIBLE);
        selectedColor.setVisibility(View.VISIBLE);
        changeFontButton.setVisibility(View.GONE);
        RadioGroup editionOptions = (RadioGroup) rootView.findViewById(R.id.editionOption);
        RadioButton filledRectangle = (RadioButton) rootView.findViewById(R.id.radio_filled_rectangle);
        filledRectangle.setChecked(true);
        int count = editionOptions.getChildCount();
        ArrayList<RadioButton> listOfRadioButtons = new ArrayList<RadioButton>();
        for (int i = 0; i < count; i++) {
            View o = editionOptions.getChildAt(i);
            if (o instanceof RadioButton) {
                listOfRadioButtons.add((RadioButton) o);
                ((RadioButton) o).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRadioButtonClicked(v);
                    }
                });
            }
        }
        this.resetEditedImage();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.resetEditedImage();
        this.refreshEditOption();
    }

    public static void changeColor(int newColor) {
        COLOR = newColor;
        selectedColor.setBackgroundColor(newColor);
        editText.setTextColor(newColor);
    }

    public void refreshEditOption() {
        switch (Action) {
            case Pen:
                description.setText(getResources().getString(R.string.penOption));
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
                changeFontButton.setVisibility(View.GONE);
                break;
            case Cut:
                description.setText(getResources().getString(R.string.cutOption));
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.GONE);
                selectedColor.setVisibility(View.GONE);
                changeFontButton.setVisibility(View.GONE);
                break;
            case Text:
                description.setText(getResources().getString(R.string.textOption));
                editText.setVisibility(View.VISIBLE);
                labelWriteText.setVisibility(View.VISIBLE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
                changeFontButton.setVisibility(View.VISIBLE);
                break;
            case FilledRectangle:
                description.setText(getResources().getString(R.string.filledRectangleOption));
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
                changeFontButton.setVisibility(View.GONE);
                break;
            case Rectangle:
                description.setText(getResources().getString(R.string.rectangleOption));
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
                changeFontButton.setVisibility(View.GONE);
                break;
            case FilledCircle:
                description.setText(getResources().getString(R.string.filledCircleOption));
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
                changeFontButton.setVisibility(View.GONE);
                break;
            case Circle:
                description.setText(getResources().getString(R.string.circleOption));
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
                changeFontButton.setVisibility(View.GONE);
                break;
        }
    }

    private void resetEditedImage() {
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        imageView.setOnTouchListener(this);
        if (MainActivity.editedPicture != null) {
            // there is a Copy to load into ImageView in Editor
            imageView.setImageBitmap(MainActivity.editedPicture);
            copy = MainActivity.editedPicture.copy(MainActivity.editedPicture.getConfig(), true);
        } else if (MainActivity.pictureToEditPath != null) {
            // open image form director, create copy, save copy in application, load image to ImageView in editor
            File imgFile = new File(MainActivity.pictureToEditPath);
            if (imgFile.exists()) {
                copy = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(copy);
                double newHeight = getResources().getDimension(R.dimen.image_edited_width) * copy.getHeight() / copy.getWidth();
                copy = copy.createScaledBitmap(copy, (int) getResources().getDimension(R.dimen.image_edited_width), (int) (newHeight), true);
                MainActivity.editedPicture = copy;
            }
        }
    }

    private void putImage() {
        MainActivity.editedPicture = copy;
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        imageView.setImageBitmap(MainActivity.editedPicture);
    }

    private void PutPixel(Bitmap bitmap, int x, int y, int color) {
        if (x < 0 || x >= bitmap.getWidth()) return;
        if (y < 0 || y >= bitmap.getHeight()) return;
        bitmap.setPixel(x, y, color);
    }

    private void DrawSegment(Point start, Point end) {
        DrawSegment(start, end, COLOR);
    }

    private void RedrawImage() {
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        if (MainActivity.editedPicture != null) {
            imageView.setImageBitmap(MainActivity.editedPicture);
            copy = MainActivity.editedPicture.copy(MainActivity.editedPicture.getConfig(), true);
        }
    }

    private void DrawSquare(double x, double y, double a, double b) {
        this.DrawSquare(x, y, a, b, COLOR);
    }

    private void DrawBorder(double x, double y, double a, double b, int scale) {
//DrawSegment - Function drawing a segment from Point A to B on bitmap
        Point LeftUpCorner = new Point((int) x, (int) y);
        Point RightUpCorner = new Point(LeftUpCorner);
        RightUpCorner.offset((int) a, 0);
        Point RightDownCorner = new Point(RightUpCorner);
        RightDownCorner.offset(0, (int) b);
        Point LeftDownCorner = new Point(RightDownCorner);
        LeftDownCorner.offset(-(int) a, 0);
        for (int i = 0; i < scale; i++) {
            DrawSegment(LeftUpCorner, RightUpCorner);
            DrawSegment(LeftUpCorner, LeftDownCorner);
            DrawSegment(RightDownCorner, RightUpCorner);
            DrawSegment(LeftDownCorner, RightDownCorner);
            LeftDownCorner.offset(-1, -1);
            LeftUpCorner.offset(-1, 1);
            RightUpCorner.offset(1, 1);
            RightDownCorner.offset(1, -1);
        }
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        imageView.setImageBitmap(copy);

    }

    private void PutPixel(float x, float y) {
        PutPixel(copy, (int) x, (int) y, COLOR);
    }

    private void DrawFilledEllipse(float x, float y, float a, float b, boolean case1) {
        float a2 = a * a;
        float b2 = b * b;
        if (a2 == 0 || b2 == 0) return;
        float d = 4 * b2 - 4 * b * a2 + a2;
        float delta_A = 4 * 3 * b2;
        float delta_B = 4 * (3 * b2 - 2 * b * a2 + 2 * a2);
        float limit = (a2 * a2) / (a2 + b2);
        float xDraw = 0;
        float yDraw = b;
        while (true) {

            if (case1) {
                for (float i = x - xDraw; i <= x + xDraw; i++) {
                    PutPixel(i, y + yDraw);
                    PutPixel(i, y - yDraw);
                }

            } else {
                for (float i = x - yDraw; i <= x + yDraw; i++) {
                    PutPixel(i, y + xDraw);
                    PutPixel(i, y - xDraw);
                }

            }
            if (xDraw * xDraw >= limit) {
                break;
            }
            if (d > 0) {
                d += delta_B;
                delta_A += 4 * 2 * b2;
                delta_B += 4 * (2 * b2 + 2 * a2);

                xDraw += 1;
                yDraw -= 1;
            } else {
                d += delta_A;
                delta_A += 4 * 2 * b2;
                delta_B += 4 * 2 * b2;

                xDraw += 1;
            }
        }
    }

    private void DrawEllipse(float x, float y, float a, float b, boolean case1) {
        float a2 = a * a;
        float b2 = b * b;
        if (a2 == 0 || b2 == 0) return;
        float d = 4 * b2 - 4 * b * a2 + a2;
        float delta_A = 4 * 3 * b2;
        float delta_B = 4 * (3 * b2 - 2 * b * a2 + 2 * a2);
        float limit = (a2 * a2) / (a2 + b2);
        float xDraw = 0;
        float yDraw = b;
        while (true) {

            if (case1) {
                PutPixel(x + xDraw, y + yDraw);
                PutPixel(x - xDraw, y + yDraw);
                PutPixel(x + xDraw, y - yDraw);
                PutPixel(x - xDraw, y - yDraw);
            } else {
                PutPixel(x + yDraw, y + xDraw);
                PutPixel(x - yDraw, y + xDraw);
                PutPixel(x + yDraw, y - xDraw);
                PutPixel(x - yDraw, y - xDraw);
            }
            if (xDraw * xDraw >= limit) {
                break;
            }
            if (d > 0) {
                d += delta_B;
                delta_A += 4 * 2 * b2;
                delta_B += 4 * (2 * b2 + 2 * a2);

                xDraw += 1;
                yDraw -= 1;
            } else {
                d += delta_A;
                delta_A += 4 * 2 * b2;
                delta_B += 4 * 2 * b2;

                xDraw += 1;
            }
        }

    }

    private void DrawEllipse(float x, float y, float a, float b) {
        DrawEllipse(x, y, a, b, true);
        DrawEllipse(x, y, b, a, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        imageView.setImageBitmap(copy);
    }

    private void DrawFilledEllipse(float x, float y, float a, float b) {
        DrawFilledEllipse(x, y, a, b, true);
        DrawFilledEllipse(x, y, b, a, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        imageView.setImageBitmap(copy);
    }

    private void recursivePenDraw(float x0, float y0, float x1, float y1) {
        if (Math.abs(x0 - x1) < 0.5 && Math.abs(x0 - x1) < 0.5) {
            return;
        }
        float x = (x0 + x1) / 2;
        float y = (y0 + y1) / 2;
        DrawSquare(x - (PENSIZE / 2), y - (PENSIZE / 2), PENSIZE, PENSIZE, COLOR);

        recursivePenDraw(x0, y0, x, y);
        recursivePenDraw(x1, y1, x, y);
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        if (imageView.getDrawable() == null) return true;
        ((ScrollView) rootView.findViewById(R.id.scrollViewEditor)).requestDisallowInterceptTouchEvent(true);
        float x = e.getX();
        float y = e.getY();
        switch (Action) {
            case FilledCircle: {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        this.RedrawImage();
                        float a = x - startX;
                        float b = y - startY;
                        a = a / 2;
                        b = b / 2;
                        float xDraw = (x + startX) / 2;
                        float yDraw = (y + startY) / 2;
                        if (a < 0) {
                            if (b < 0) {
                                DrawFilledEllipse(xDraw, yDraw, -a, -b);
                            } else {
                                DrawFilledEllipse(xDraw, yDraw, -a, b);

                            }
                        } else {
                            if (b < 0) {
                                DrawFilledEllipse(xDraw, yDraw, a, -b);
                            } else {
                                DrawFilledEllipse(xDraw, yDraw, a, b);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        putImage();
                        break;
                }

            }
            break;
            case Circle: {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        this.RedrawImage();
                        float a = x - startX;
                        float b = y - startY;
                        a = a / 2;
                        b = b / 2;
                        float xDraw = (x + startX) / 2;
                        float yDraw = (y + startY) / 2;
                        if (a < 0) {
                            if (b < 0) {
                                DrawEllipse(xDraw, yDraw, -a, -b);
                            } else {
                                DrawEllipse(xDraw, yDraw, -a, b);

                            }
                        } else {
                            if (b < 0) {
                                DrawEllipse(xDraw, yDraw, a, -b);
                            } else {
                                DrawEllipse(xDraw, yDraw, a, b);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        putImage();
                        break;
                }

            }
            break;
            case Pen:
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        recursivePenDraw(startX, startY, x, y);
                        startX = x;
                        startY = y;
                        putImage();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;
                        DrawSquare(startX - (PENSIZE / 2), startY - (PENSIZE / 2), PENSIZE, PENSIZE, COLOR);

                        break;
                    case MotionEvent.ACTION_UP:
                        putImage();
                        break;
                }
                break;
            case Rectangle:
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        RedrawImage(); // Image without border
                        DrawBorder(startX, startY, x - startX, y - startY, 2);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        double dx = x - startX;
                        double dy = y - startY;
                        RedrawImage();
                        DrawBorder(startX, startY, dx, dy, 2);
                        putImage(); //save border on image
                }
                break;
            case FilledRectangle:
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        RedrawImage(); // Image without border
                        DrawSquare(startX, startY, x - startX, y - startY);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        double dx = x - startX;
                        double dy = y - startY;
                        RedrawImage();
                        DrawSquare(startX, startY, x - startX, y - startY);
                        putImage(); //save border on image
                }
                break;
            case Text:
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    //Create Text Box and take input
                    drawText(editText.getText().toString(), x, y, COLOR);
                    putImage();
                }
                break;
            case Cut:
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        RedrawImage(); // Image without border
                        DrawBorder(startX, startY, x - startX, y - startY, 1);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        double dx = x - startX;
                        double dy = y - startY;
                        RedrawImage();
                        DrawSquare(startX, startY, dx, dy, Color.WHITE);
                        putImage(); //save border on image
                }
        }
        return true;
    }

    private void drawText(String Text, double x, double y, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(20);
        try {
            paint.setTypeface(Typeface.create(FONT_STYLE, FONT_ADDITIONAL_STYLE));
        } catch (Exception e) {
            paint.setTypeface(Typeface.create("Helvetica", Typeface.NORMAL));
        }
        Canvas canvas = new Canvas(copy);
        canvas.drawText(Text, (float) x, (float) y, paint);
    }

    private void DrawSquare(double x, double y, double a, double b, int color) {
        if (a >= 0) {
            int i;
            for (i = (int) x; i <= (int) (x + a);
                 i++)
                DrawSegment(new Point(i, (int) y), new Point(i, (int) (y + b)), color);
        } else {
            int i;
            for (i = (int) x; i >= (int) (x + a);
                 i--)
                DrawSegment(new Point(i, (int) y), new Point(i, (int) (y + b)), color);
        }
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        imageView.setImageBitmap(copy);
    }

    private void DrawSegment(Point start, Point end, int color) {
        if (start.x == end.x) {
            int lowerBound = Math.min(start.y, end.y);
            int upperBound = Math.max(start.y, end.y);
            for (int i = lowerBound; i <= upperBound; i++) PutPixel(copy, start.x, i, color);
        } else {
            int lowerBound = Math.min(start.x, end.x);
            int upperBound = Math.max(start.x, end.x);
            for (int i = lowerBound; i <= upperBound; i++) PutPixel(copy, i, start.y, color);
        }
    }

    // switching edition mode
    private void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_pen:
                if (checked) {
                    Action = EditActionType.Pen;
                }
                break;
            case R.id.radio_rectangle:
                if (checked) {
                    Action = EditActionType.Rectangle;
                }
                break;
            case R.id.radio_filled_rectangle:
                if (checked) {
                    Action = EditActionType.FilledRectangle;
                }
                break;
            case R.id.radio_circle:
                if (checked) {
                    Action = EditActionType.Circle;
                }
                break;
            case R.id.radio_filled_circle:
                if (checked) {
                    Action = EditActionType.FilledCircle;
                }
                break;
            case R.id.radio_text:
                if (checked) {
                    Action = EditActionType.Text;
                }
                break;
            case R.id.radio_cut:
                if (checked) {
                    Action = EditActionType.Cut;
                }
                break;
        }
        refreshEditOption();
    }
}