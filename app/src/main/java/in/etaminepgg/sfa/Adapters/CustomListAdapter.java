package in.etaminepgg.sfa.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.etaminepgg.sfa.Models.SkuGroupHistory;
import in.etaminepgg.sfa.R;

public class CustomListAdapter extends ArrayAdapter<SkuGroupHistory>
{
    ArrayList<SkuGroupHistory> skuItemNameList,tempCustomer, suggestions ;


    public interface CustomItemClickListener {
        public void onItemClick(View v, int position,SkuGroupHistory skuGroupHistory);
    }

    CustomItemClickListener listener;

  /*  public CustomListAdapter(Context context,ArrayList<SkuGroupHistory> objects,CustomItemClickListener listener) {
        super(context, android.R.layout.simple_dropdown_item_1line, objects);
        this.skuItemNameList = objects ;
        this.tempCustomer = new ArrayList<SkuGroupHistory>(objects);
        this.suggestions = new ArrayList<SkuGroupHistory>(objects);
        this.listener=listener;

    }*/  public CustomListAdapter(Context context,ArrayList<SkuGroupHistory> objects) {
        super(context, R.layout.itemrowsearch, objects);
        this.skuItemNameList = objects ;
        this.tempCustomer = new ArrayList<SkuGroupHistory>(objects);
        this.suggestions = new ArrayList<SkuGroupHistory>(objects);


    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SkuGroupHistory customer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.itemrowsearch, parent, false);
        }
        TextView txtCustomer = (TextView) convertView.findViewById(R.id.textView);

        if (txtCustomer != null)
            txtCustomer.setText(customer.toString());

       /* convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listener.onItemClick(view,position,suggestions.get(position));
            }
        });*/

        return convertView;
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            SkuGroupHistory customer = (SkuGroupHistory) resultValue;
            return customer.toString();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (SkuGroupHistory people : tempCustomer) {
                    String str=people.toString().replaceAll("[^a-zA-Z0-9\\s]", "");
                    if (people.toString().toLowerCase().contains(constraint.toString().toLowerCase()) || people.toString().toLowerCase().equalsIgnoreCase(constraint.toString())) {
                        suggestions.add(people);
                    }else if (str.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }/*else if (people.toString().toLowerCase().contains(constraint.toString().substring(0,2).toLowerCase())) {
                        suggestions.add(people);
                    }*/
                    else{
                        System.out.println("Did not find a match");
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<SkuGroupHistory> c = (ArrayList<SkuGroupHistory>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (SkuGroupHistory cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
