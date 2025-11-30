``` java
 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
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

              // Menu item tint for single menu
              MenuItem menuItem = menu.findItem(R.id.action_screen_saver);
               if (menuItem != null && menuItem.getIcon() != null) {
                  menuItem.getIcon().setTint(ContextCompat.getColor(this, R.color.redx));
               }
           }
        return true;
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

}
 