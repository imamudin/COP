package listAdapter;

/**
 * Created by agung on 07/03/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.imamudin.cop.R;

import java.util.List;

import app.MyAppController;
import model.CariKasusItem;

public class ListAdapterCariKasus extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<CariKasusItem> cariKasusItems;
    ImageLoader imageLoader = MyAppController.getInstance().getImageLoader();

    public ListAdapterCariKasus(Activity activity, List<CariKasusItem> cariKasusItems) {
        this.activity = activity;
        this.cariKasusItems = cariKasusItems;
    }

    @Override
    public int getCount() {
        return cariKasusItems.size();
    }

    @Override
    public Object getItem(int location) {
        return cariKasusItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_cari_kasus, null);


        TextView nama_kasus  = (TextView) convertView.findViewById(R.id.tv_nama_kasus);
        TextView nama_pelapor= (TextView) convertView.findViewById(R.id.tv_nama_pelapor);
        TextView no_lp       = (TextView) convertView.findViewById(R.id.tv_no_lp);
        TextView tgl_kejadian= (TextView) convertView.findViewById(R.id.tv_tgl_kejadian);

        // getting billionaires data for the row
        CariKasusItem m = cariKasusItems.get(position);

        nama_kasus.setText(m.getNama_kasus());
        nama_pelapor.setText(m.getNama_pelapor());
        tgl_kejadian.setText(m.getTgl_kasus().substring(0,10));
        no_lp.setText(m.getNo_lp());

        return convertView;
    }

}
