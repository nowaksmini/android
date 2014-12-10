package mem.memenator.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import java.io.File;

import mem.memenator.EditAction;
import mem.memenator.MainActivity;
import mem.memenator.R;


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
    public static EditAction Action;
    private double startx;
    private double starty;
    public static int COLOR = Color.BLACK;
    private static EditText editText;
    private TextView description;
    private TextView labelWriteText;
    private Button changeColorButton;
    private static TextView  selectedColor;
    private ImageView optionEditionImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bundle = mapping String on Parcelable Type
        Action = EditAction.Text;
        rootView = inflater.inflate(R.layout.editor_fragment, container, false);
        editText = (EditText) rootView.findViewById(R.id.textToPasteIntoEditedImage);
        description = (TextView) rootView.findViewById(R.id.editorDescription);
        optionEditionImage = (ImageView) rootView.findViewById(R.id.optionEditionImage);
        labelWriteText = (TextView) rootView.findViewById(R.id.labelWriteText);
        changeColorButton = (Button) rootView.findViewById(R.id.changeColorButton);
        selectedColor = (TextView) rootView.findViewById(R.id.selectedColor);
        selectedColor.setBackgroundColor(COLOR);
        editText.setTextColor(COLOR);
        if(optionEditionImage.getDrawable()==null) optionEditionImage.setBackgroundResource(R.drawable.ic_text);
        this.resetEditedImage();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.resetEditedImage();
    }

    public static void changeColor(int newColor)
    {
        COLOR = newColor;
        selectedColor.setBackgroundColor(newColor);
        editText.setTextColor(newColor);

    }
    public void refreshEditOption()
    {
        switch(Action)
        {
            case Cut:
                description.setText(getResources().getString(R.string.cutOption));
                optionEditionImage.setBackgroundResource(R.drawable.ic_cut);
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.GONE);
                selectedColor.setVisibility(View.GONE);
                break;
            case Text:
                description.setText(getResources().getString(R.string.textOption));
                optionEditionImage.setBackgroundResource(R.drawable.ic_text);
                editText.setVisibility(View.VISIBLE);
                labelWriteText.setVisibility(View.VISIBLE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
                break;
            case SolidBrush:
                description.setText(getResources().getString(R.string.solidBrushOption));
                optionEditionImage.setBackgroundResource(R.drawable.ic_brush);
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
                break;
            case Border:
                description.setText(getResources().getString(R.string.borderBrushOption));
                optionEditionImage.setBackgroundResource(R.drawable.ic_border);
                editText.setVisibility(View.GONE);
                labelWriteText.setVisibility(View.GONE);
                changeColorButton.setVisibility(View.VISIBLE);
                selectedColor.setVisibility(View.VISIBLE);
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

    public void DrawSquare(double x, double y, double a, double b) {
        this.DrawSquare(x, y, a, b, COLOR);
    }

    public void DrawBorder(double x, double y, double a, double b, int scale) {
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

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        ImageView imageView = (ImageView) rootView.findViewById(R.id.editorImageView);
        if(imageView.getDrawable() == null) return true;
        ((ScrollView) rootView.findViewById(R.id.scrollViewEditor)).requestDisallowInterceptTouchEvent(true);
        float x = e.getX();
        float y = e.getY();
        switch (Action) {
            case Border:
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        RedrawImage(); // Image without border
                        DrawBorder(startx, starty, x - startx, y - starty, 2);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startx = x;
                        starty = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        double dx = x - startx;
                        double dy = y - starty;
                        RedrawImage();
                        DrawBorder(startx, starty, x - startx, y - starty, 2);
                        putImage(); //save border on image
                }
                break;
            case SolidBrush:
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        RedrawImage(); // Image without border
                        DrawSquare(startx, starty, x - startx, y - starty);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startx = x;
                        starty = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        double dx = x - startx;
                        double dy = y - starty;
                        RedrawImage();
                        DrawSquare(startx, starty, x - startx, y - starty);
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
                        DrawBorder(startx, starty, x - startx, y - starty, 1);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startx = x;
                        starty = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        double dx = x - startx;
                        double dy = y - starty;
                        RedrawImage();
                        DrawSquare(startx, starty, dx, dy, Color.WHITE);
                        putImage(); //save border on image
                }
        }
        return true;
    }

    private void drawText(String Text, double x, double y, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(20);
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
}