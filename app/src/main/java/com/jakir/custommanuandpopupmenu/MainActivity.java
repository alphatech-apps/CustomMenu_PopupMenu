package com.jakir.custommanuandpopupmenu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button button;
    MaterialToolbar toolbar;
    Menu menu1;
    boolean itemChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        setSupportActionBar(toolbar);


        button = findViewById(R.id.button);

        button.setOnClickListener(veiw -> showStyledPopup(veiw));

        ImageHelper helper = new ImageHelper(this);
        helper.downloadAndSaveImage("https://lh3.googleusercontent.com/ogw/AF2bZygr5vsSBld4weUesEb7t7Y6nhq_s7GEgK2WW4Klbhrdur0=s32-c-mo",
                (bitmapx, savedFile) -> {

            MenuItem menuItem = menu1.findItem(R.id.action_account);
            if (menuItem != null && menuItem.getIcon() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir() + "/account_pic.png");
                Drawable drawable = getCircularDrawableWithBorder(bitmapx, itemChecked);
                if (drawable != null) {
                    menuItem.setIcon(drawable);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        menu1 = menu;
        if (menu != null) {

            // Overflow icon tint
            Drawable overflow = toolbar.getOverflowIcon();
            if (overflow != null) {
                overflow.setTint(ContextCompat.getColor(this, R.color.redx));
            }

            // Menu item tint for all items of menu
            for (int i = 0; i < menu.size(); i++) {
                Drawable icon = menu.getItem(i).getIcon();
                if (icon != null) {
                    icon.setTint(ContextCompat.getColor(this, R.color.redx));
                }
            }
/*
            // Menu item tint for single menu
            MenuItem menuItem = menu.findItem(R.id.action_screen_saver);
            if (menuItem != null && menuItem.getIcon() != null) {
                menuItem.getIcon().setTint(ContextCompat.getColor(this, R.color.redx));
            }
*/


        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.findItem(R.id.action_account);
        if (menuItem != null && menuItem.getIcon() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir() + "/account_pic.png");

//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aaa);
            Drawable drawable = getCircularDrawableWithBorder(bitmap, itemChecked);
            if (drawable != null) {
                menuItem.setIcon(drawable);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) { // its 3dot overflow menu
        if (menu != null) {
            // Force to show icons on overflow menu
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception ignore) {
                }
            }

            // Force to set padding on overflow menu
            try {
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    View actionView = findViewById(item.getItemId());

                    if (actionView == null) { // overflow items normally have no actionView
                        Drawable icon = item.getIcon();

                        if (icon != null && !(icon instanceof InsetDrawable)) { // check before applying
                            int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
                            item.setIcon(new InsetDrawable(icon, marginPx, 0, marginPx, 0));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_account) {
            item.setCheckable(true);
            itemChecked = !itemChecked;
            Objects.requireNonNull(item.getIcon()).setTintList(null);

            invalidateOptionsMenu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void showStyledPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.my_menu);

        // Force show icons on popup menu
        try {
            Field mPopup = popupMenu.getClass().getDeclaredField("mPopup");
            mPopup.setAccessible(true);
            Object menuPopupHelper = mPopup.get(popupMenu);
            Method setForceIcons = Objects.requireNonNull(menuPopupHelper).getClass().getMethod("setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception ignore) {
        }

        // Force to set padding on popup menu
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            MenuItem item = popupMenu.getMenu().getItem(i);
            Drawable icon = item.getIcon();
            if (icon != null && !(icon instanceof InsetDrawable)) { // check before applying
                int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
                item.setIcon(new InsetDrawable(icon, marginPx, 0, marginPx, 0));
            }
        }

        // Popup Menu item tint for all items of menu
        Menu menu = popupMenu.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            Drawable icon = menu.getItem(i).getIcon();
            if (icon != null) {
                icon.setTint(ContextCompat.getColor(this, R.color.redx2));
            }
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            return false;
        });

        popupMenu.show();
    }


    private Drawable getCircularDrawableWithBorder(Bitmap bitmap, boolean checked) {
        if (bitmap == null) return null;

        // Convert 24dp to pixels
        int sizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27, getResources().getDisplayMetrics());

        // Resize bitmap to 24x24
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, sizePx, sizePx, true);

        Bitmap output = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        float centerX = sizePx / 2f;
        float centerY = sizePx / 2f;
        float radius = sizePx / 2f;

        float shrinkFactor = 0f;

        if (checked) {
            // Adjust radius for inner stroke padding
            float outerStroke = 6f;
            float innerStroke = 4f;
            shrinkFactor = (outerStroke + innerStroke) / 2f;
        }

        float imageRadius = radius - shrinkFactor;

        // Draw circular bitmap
        BitmapShader shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawCircle(centerX, centerY, imageRadius, paint);

        TypedValue typedValue = new TypedValue();

        getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimaryVariant, typedValue, true);
        int outerColor = typedValue.data;

        getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true);
        int innerColor = typedValue.data;

        // Draw strokes if checked
        if (checked) {
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);

            // Outer black stroke
            paint.setStrokeWidth(4f);
            paint.setColor(outerColor);
            canvas.drawCircle(centerX, centerY, radius - 3f, paint);

            // Inner red stroke
            paint.setStrokeWidth(1.5f);
            paint.setColor(innerColor);
            canvas.drawCircle(centerX, centerY, radius - 5f, paint);
        }

        return new BitmapDrawable(getResources(), output);
    }

    private void chengeAccountIcon0() {
        // Menu item tint for single menu
        MenuItem menuItem = menu1.findItem(R.id.action_account);
        if (menuItem != null && menuItem.getIcon() != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aaa);
            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedDrawable.setCircular(true); // circular icon
            int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            menuItem.setIcon(new InsetDrawable(roundedDrawable, marginPx, 0, marginPx, 0));
        }
    }

    private Bitmap makeRoundedCropped(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // আরও ছোট area crop করি (YouTube profile এর মতো)
        int cropSize = (int) (Math.min(width, height) * 0.80f); // 80% রাখলাম
        int left = (width - cropSize) / 2;
        int top = (height - cropSize) / 3;  // উপরের দিকে বেশি কাটবে
        if (top < 0) top = 0;

        Bitmap cropped = Bitmap.createBitmap(bitmap, left, top, cropSize, cropSize);

        // এখন circle বানাই
        Bitmap output = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        float radius = cropSize / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(cropped, 0, 0, paint);

        return output;
    }


}
