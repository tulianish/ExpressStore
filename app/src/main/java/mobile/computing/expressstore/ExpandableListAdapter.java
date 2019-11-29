package mobile.computing.expressstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;
import java.util.List;

/*
ExpandableListAdapter
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> dates;
    //private HashMap<String,String> itemDetails;
    private LinkedHashMap<String, List<Items>> orderDetails;


    public ExpandableListAdapter(Context context, List<String> dates, LinkedHashMap<String, List<Items>> orderDetails) {
        this.context = context;
        this.dates = dates;
        this.orderDetails = orderDetails;
    }

    @Override
    public int getGroupCount() {
        return this.dates.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.orderDetails.get(this.dates.get(i))
                .size();
    }

    @Override
    public Object getGroup(int i) {
        return this.dates.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.orderDetails.get(this.dates.get(i))
                .get(i1);

    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {

        String dateOfOrder = (String) getGroup(i);
        View v=convertView;
        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.expandable_list_group, null);
        }
        TextView dateOfOrderHeader = v.findViewById(R.id.dateOfOrder);
        dateOfOrderHeader.setText(dateOfOrder);
        return v;

    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {

        View v = convertView;
        Items item=(Items) getChild(i,i1);
       // final String expandedListText = (String) getChild(i, i1);

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.expandable_list_item, null);
        }
        TextView nameOfProduct =  v.findViewById(R.id.nameOfProduct);
        TextView priceOfProduct =  v.findViewById(R.id.priceOfProduct);
        ImageView prodImage=v.findViewById(R.id.prodImage);
        nameOfProduct.setText(item.getName());
        priceOfProduct.setText(item.getPrice());
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(item.getImgUrl()).resize(65,65).into(prodImage);

        return v;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
