package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by brianroper on 5/3/16.
 */
public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {

            private Cursor cursor = null;
            private String symbol = "";
            private String bidPrice = "";
            private String change = "";
            private int isUp;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {

                final long callingIdentity = Binder.clearCallingIdentity();

                if(cursor != null){
                    cursor.close();
                }

                cursor = getContentResolver()
                        .query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{
                                QuoteColumns._ID,
                                QuoteColumns.SYMBOL,
                                QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE,
                                QuoteColumns.CHANGE,
                                QuoteColumns.ISUP
                        },
                                QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(callingIdentity);
            }

            @Override
            public void onDestroy() {

                cursor.close();
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.list_item_quote);

                symbol = cursor.getString(cursor.getColumnIndex("symbol"));
                bidPrice = cursor.getString(cursor.getColumnIndex("bid_price"));
                change = cursor.getString(cursor.getColumnIndex("percent_change"));
                isUp = cursor.getInt(cursor.getColumnIndex("is_up"));

                remoteViews.setTextViewText(R.id.stock_symbol, symbol);
                remoteViews.setTextViewText(R.id.bid_price, bidPrice);
                remoteViews.setTextViewText(R.id.change, change);

                if(isUp == 1){

                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                }
                else{

                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                final Intent intent = new Intent();
                intent.putExtra("symbol", symbol);
                remoteViews.setOnClickFillInIntent(R.layout.list_item_quote, intent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 4;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position))
                    return cursor.getLong(cursor.getColumnIndexOrThrow(QuoteColumns._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
