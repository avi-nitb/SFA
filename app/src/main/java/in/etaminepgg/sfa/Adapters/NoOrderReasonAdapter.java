package in.etaminepgg.sfa.Adapters;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import in.etaminepgg.sfa.Models.NoReasonModel;
import in.etaminepgg.sfa.R;

/**
 * Created by etamine on 19/2/18.
 */

public class NoOrderReasonAdapter extends RecyclerView.Adapter<NoOrderReasonAdapter.ReasonInfoViewHolder>
{

    Context context;
    ArrayList<NoReasonModel> noReasonModelArrayList;

    public static HashMap<String,String> selected_Reason_hashmap=new HashMap<>();

    public NoOrderReasonAdapter(Context context, ArrayList<NoReasonModel> noReasonModelArrayList)
    {
        this.context = context;
        this.noReasonModelArrayList = noReasonModelArrayList;
    }

    @Override
    public NoOrderReasonAdapter.ReasonInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_noreason, parent, false);
        return new NoOrderReasonAdapter.ReasonInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NoOrderReasonAdapter.ReasonInfoViewHolder holder, int position)
    {

        final NoReasonModel noReasonModel = noReasonModelArrayList.get(position);
        holder.tv_noreason.setText(noReasonModel.getReason_desc());
        if (noReasonModel.isIschecked())
        {
            holder.tv_noreason.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_black_24dp, 0, 0, 0);

            if (noReasonModel.getReason_id().equalsIgnoreCase("4"))
            {

                holder.noreason_header.setVisibility(View.VISIBLE);
                holder.whyNoOrder_TextInputEditText.setVisibility(View.VISIBLE);
                //holder.txtlay_noreason.setVisibility(View.VISIBLE);

                selected_Reason_hashmap.put(noReasonModel.getReason_id(),"other_"+holder.whyNoOrder_TextInputEditText.getText().toString());
            }
            else
            {
                holder.noreason_header.setVisibility(View.GONE);
                holder.whyNoOrder_TextInputEditText.setVisibility(View.GONE);
               // holder.txtlay_noreason.setVisibility(View.GONE);

                selected_Reason_hashmap.put(noReasonModel.getReason_id(),noReasonModel.getReason_desc());
            }

            Log.e("selectedreason",selected_Reason_hashmap.toString());

        }
        else
        {
            holder.tv_noreason.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_outline_blank_black_24dp, 0, 0, 0);
            holder.noreason_header.setVisibility(View.GONE);
            holder.whyNoOrder_TextInputEditText.setVisibility(View.GONE);
          //  holder.txtlay_noreason.setVisibility(View.GONE);

            if(selected_Reason_hashmap.containsKey(noReasonModel.getReason_id())){

                selected_Reason_hashmap.remove(noReasonModel.getReason_id());
            }

            Log.e("selectedreason",selected_Reason_hashmap.toString());
        }

        holder.whyNoOrder_TextInputEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                selected_Reason_hashmap.put(noReasonModel.getReason_id(),holder.whyNoOrder_TextInputEditText.getText().toString());

                Log.e("selectedreason",selected_Reason_hashmap.toString());
            }
        });

        holder.tv_noreason.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                boolean status = noReasonModel.isIschecked();
                noReasonModel.setIschecked(!status);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return noReasonModelArrayList.size();
    }

    public class ReasonInfoViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_noreason;
        TextView noreason_header;
        AppCompatEditText whyNoOrder_TextInputEditText;
       // TextInputEditText whyNoOrder_TextInputEditText;
      //  TextInputLayout txtlay_noreason;

        public ReasonInfoViewHolder(View itemView)
        {
            super(itemView);
            tv_noreason = (TextView) itemView.findViewById(R.id.tv_noreason);
            noreason_header = (TextView) itemView.findViewById(R.id.noreason_header);
            whyNoOrder_TextInputEditText = (AppCompatEditText) itemView.findViewById(R.id.whyNoOrder_TextInputEditText);
          //  txtlay_noreason = (TextInputLayout) itemView.findViewById(R.id.txtlay_noreason);
        }
    }
}
