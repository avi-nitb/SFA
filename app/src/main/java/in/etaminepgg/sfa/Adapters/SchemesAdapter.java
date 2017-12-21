package in.etaminepgg.sfa.Adapters;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.etaminepgg.sfa.Activities.LoginActivity;
import in.etaminepgg.sfa.Models.Scheme;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.MyDb;
import in.etaminepgg.sfa.Utilities.Utils;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SCHEME;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;

/**
 * Created by etamine on 29/8/17.
 */

public class SchemesAdapter extends RecyclerView.Adapter<SchemesAdapter.SchemeInfoViewHolder>
{
    private List<Scheme> schemeList;

    public SchemesAdapter(List<Scheme> schemeList)
    {
        this.schemeList = schemeList;
    }

    @Override
    public SchemeInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_scheme, parent, false);
        return new SchemesAdapter.SchemeInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SchemeInfoViewHolder schemeInfoViewHolder, int position)
    {
        String schemeID = schemeList.get(position).getID();
        String schemeName = schemeList.get(position).getName();
        String schemeDescription = schemeList.get(position).getDescription();
        String schemeStartDate = schemeList.get(position).getStartDate();
        String schemeEndDate = schemeList.get(position).getEndDate();

        schemeInfoViewHolder.itemView.setTag(schemeID);

        schemeInfoViewHolder.schemeName_TextView.setText(schemeName);
        schemeInfoViewHolder.schemeDescription_TextView.setText(schemeDescription);
        schemeInfoViewHolder.schemeStartDate_TextView.setText("Started On: " + schemeStartDate);
        schemeInfoViewHolder.schemeEndDate_TextView.setText("Expires On: " + schemeEndDate);
    }

    @Override
    public int getItemCount()
    {
        return schemeList.size();
    }

    class SchemeInfoViewHolder extends RecyclerView.ViewHolder
    {
        TextView schemeName_TextView, schemeDescription_TextView, schemeStartDate_TextView, schemeEndDate_TextView;

        SchemeInfoViewHolder(final View itemView)
        {
            super(itemView);

            schemeName_TextView = (TextView) itemView.findViewById(R.id.schemeName_TextView);
            schemeDescription_TextView = (TextView) itemView.findViewById(R.id.schemeDescription_TextView);
            schemeStartDate_TextView = (TextView) itemView.findViewById(R.id.schemeStartDate_TextView);
            schemeEndDate_TextView = (TextView) itemView.findViewById(R.id.schemeEndDate_TextView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    String schemeID = itemView.getTag().toString();
                    updateSchemeAsViewed(schemeID);

                    Utils.showToast(LoginActivity.baseContext, schemeID + " is viewed");

                    int position = getLayoutPosition();
                    schemeList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, schemeList.size());
                }
            });
        }

        private void updateSchemeAsViewed(String schemeID)
        {
            int valueFromOpenDatabase = MyDb.openDatabase(dbFileFullPath);
            SQLiteDatabase sqLiteDatabase = MyDb.getDbHandle(valueFromOpenDatabase);

            ContentValues schemesValues = new ContentValues();
            schemesValues.put("is_viewed", "1");
            sqLiteDatabase.update(TBL_SCHEME, schemesValues, "scheme_id = ?", new String[]{schemeID});

            sqLiteDatabase.close();
        }
    }
}
