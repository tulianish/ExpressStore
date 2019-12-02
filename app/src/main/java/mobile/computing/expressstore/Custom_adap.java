package mobile.computing.expressstore;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Custom_adap extends RecyclerView.Adapter<Custom_adap.ItemViewHolder> {
    /**
     * Custom_adap - This class acts as a custom adapter for displaying the products to be bought in a recycler view.
     *
     * Attributes:
     *      con (Context): Saves the context of the activity that uses this adapter
     *      inflater (LayoutInflater): LayaoutInflater object initialized based on the context
     *      cartitems (double): Uses the Items_Model class to get the details of the products added to cart
     */

    Context con;
    LayoutInflater inflater;
    List<Items_Model> cartitems;

    public Custom_adap(Context con, List<Items_Model> cartitems) {
        this.con = con;
        this.inflater = LayoutInflater.from(con);
        this.cartitems = cartitems;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

        String img_url = cartitems.get(position).getImg();
        String name = cartitems.get(position).getName();
        double price = cartitems.get(position).getPrice();
        double sale_price = cartitems.get(position).getSale_price();
        int qty = cartitems.get(position).getQty();

        CartActivity.total_amt += qty*sale_price;
        if(position==cartitems.size()-1) {
            CartActivity.tv_total_amt.setText("$" + String.format("%.2f", CartActivity.total_amt));
        }

        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(img_url).resize(65,65).into(holder.item_img);

        holder.item_name.setText(name);
        holder.item_name.setSelected(true);

        //Toast.makeText(con, price+" "+sale_price, Toast.LENGTH_SHORT).show();

        holder.tv_item_qty.setText("x"+qty);

        holder.item_price.setPaintFlags(holder.item_price.getPaintFlags() | Paint.ANTI_ALIAS_FLAG);
        holder.item_sale_price.setVisibility(View.VISIBLE);

        if(price == sale_price){
            holder.item_price.setText("$"+price+"");
            holder.item_sale_price.setVisibility(View.INVISIBLE);
        }else if(price > sale_price){
            holder.item_price.setText("$"+price+"");
            holder.item_price.setPaintFlags(holder.item_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.item_sale_price.setText("$"+sale_price+"");
            //holder.item_sale_price.setTextColor(Color.parseColor("#00cc00"));
        }

        final int curr_qty = cartitems.get(position).getQty();

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CartActivity.total_amt=0.0;

                cartitems.get(position).setQty(curr_qty+1);
                CartActivity.items.get(position).setQty(curr_qty+1);
                CartActivity.adap.notifyDataSetChanged();
            }
        });

        holder.btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CartActivity.total_amt=0.0;

                cartitems.get(position).setQty(curr_qty-1);
                CartActivity.items.get(position).setQty(curr_qty-1);
                CartActivity.adap.notifyDataSetChanged();
            }
        });

    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        ImageView item_img;
        TextView item_name;
        TextView item_price;
        TextView item_sale_price;
        TextView tv_item_qty;
        Button btn_add;
        Button btn_sub;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            item_img = itemView.findViewById(R.id.item_img);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);
            item_sale_price = itemView.findViewById(R.id.item_sale_price);
            btn_add = itemView.findViewById(R.id.btn_add);
            btn_sub = itemView.findViewById(R.id.btn_sub);
            tv_item_qty = itemView.findViewById(R.id.tv_item_qty);
        }
    }

    @Override
    public int getItemCount() {
        return cartitems.size();
    }
}
