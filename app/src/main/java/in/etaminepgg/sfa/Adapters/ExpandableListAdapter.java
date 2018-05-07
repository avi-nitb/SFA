package in.etaminepgg.sfa.Adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import in.etaminepgg.sfa.Models.CategoryHeader;
import in.etaminepgg.sfa.Models.SubCategoryForCategoryHeader;
import in.etaminepgg.sfa.R;

//For expandable list view use BaseExpandableListAdapter
public class ExpandableListAdapter extends BaseExpandableListAdapter {
	private Context _context;
	private List<CategoryHeader> header; // header titles
	// Child data in format of header title, child title
	private HashMap<String, List<SubCategoryForCategoryHeader>> child;

	public ExpandableListAdapter(Context context, List<CategoryHeader> listDataHeader,
			HashMap<String, List<SubCategoryForCategoryHeader>> listChildData) {
		this._context = context;
		this.header = listDataHeader;
		this.child = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {

		// This will return the child
		return this.child.get(this.header.get(groupPosition).getCategory_id()).get(
				childPosititon).getSub_category_name();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		//return childPosition;
		return Long.parseLong(this.child.get(this.header.get(groupPosition).getCategory_id()).get(childPosition).getSub_category_id());
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		// Getting child text
		final String childText = (String) getChild(groupPosition, childPosition);

		// Inflating child layout and setting textview
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.childs, parent, false);
		}

		TextView child_text = (TextView) convertView.findViewById(R.id.child);

		child_text.setText(childText);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		// return children count
		return this.child.get(this.header.get(groupPosition).getCategory_id()).size();
	}

	@Override
	public Object getGroup(int groupPosition) {

		// Get header position
		return this.header.get(groupPosition).getCategory_name();
	}

	@Override
	public int getGroupCount() {

		// Get header size
		return this.header.size();
	}

	@Override
	public long getGroupId(int groupPosition) {

		//return groupPosition;

		return Long.parseLong(this.header.get(groupPosition).getCategory_id());
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		// Getting header title
		String headerTitle = (String) getGroup(groupPosition);

		// Inflating header layout and setting text
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.header, parent, false);
		}

		TextView header_text = (TextView) convertView.findViewById(R.id.header);

		header_text.setText(headerTitle);

		// If group is expanded then change the text into bold and change the
		// icon
		if (isExpanded) {
			header_text.setTypeface(null, Typeface.BOLD);
			header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_up, 0);
		} else {
			// If group is not expanded then change the text back into normal
			// and change the icon

			header_text.setTypeface(null, Typeface.NORMAL);
			header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_down, 0);
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}