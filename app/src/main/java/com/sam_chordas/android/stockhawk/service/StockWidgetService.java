package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.database.Cursor;
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

            private Cursor cursor;
            private String symbol = "";
            private String bidPrice = "";
            private String change = "";
            private int isUp;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {


                cursor = getContentResolver()
                        .query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{
                                QuoteColumns._ID,
                                QuoteColumns.BIDPRICE,
                                QuoteColumns.CHANGE,
                                QuoteColumns.ISUP,
                                QuoteColumns.SYMBOL,
                                QuoteColumns.PERCENT_CHANGE
                        },
                                QuoteColumns.ISCURRENT,
                        new String[]{"1"},
                        null);

                cursor.close();
            }

            @Override
            public void onDestroy() {

                cursor.close();
            }

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public RemoteViews getViewAt(int position) {

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

                    remoteViews.setInt(R.id.change, "setBackgroundResouce", R.drawable.percent_change_pill_red);
                }


                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 0;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
