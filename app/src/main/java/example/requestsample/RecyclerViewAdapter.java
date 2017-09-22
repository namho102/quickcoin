package example.requestsample;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ArrayList<DataModel> mValues;
    protected ItemListener mListener;
    int itemInitCount = 0;

    public RecyclerViewAdapter(ArrayList<DataModel> values) {
        mValues = values;
    }

    public void updateList(ArrayList<DataModel> values) {
        mValues = values;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView coinNameView;
        public TextView priceView;
        public ImageView logoView;
        public RelativeLayout relativeLayout;
        DataModel item;

        public ViewHolder(View v) {

            super(v);

            v.setOnClickListener(this);
            coinNameView = (TextView) v.findViewById(R.id.coinNameView);
            priceView = (TextView) v.findViewById(R.id.priceView);
            logoView = (ImageView) v.findViewById(R.id.imageView);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);

        }

        public void setData(final DataModel item) {
            if(this.item != null) {
                System.out.println(this.item.price);
            }

//            System.out.println("new price" + item.price);
            this.item = item;

            System.out.println(item.price);
            priceView.setText("$" + String.format("%.2f", item.price));

            if(itemInitCount != getItemCount()) {
                coinNameView.setText(item.coinId);

                Context context = logoView.getContext();
                int imageId = context.getResources().getIdentifier(item.coinId.toLowerCase(), "drawable", context.getPackageName());
                logoView.setImageResource(imageId);

//            logoView.setImageResource(R.drawable.BTC);

//                relativeLayout.setBackgroundColor(Color.parseColor(item.color));
                itemInitCount++;
            }

        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position) {
        Vholder.setData(mValues.get(position));

    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(DataModel item);
    }
}