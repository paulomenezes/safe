package menezes.paulo.safe;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import menezes.paulo.safe.adapter.ListReportItemAdapter;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Report;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class AllReportsActivity extends Activity {
	MobileServiceClient mClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_reports);

		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
		
		mClient = Connect.getInstance();

		Bundle b = getIntent().getExtras();
		if(b != null && b.getInt("userID") != -1) {
		    mClient.getTable(Report.class).where().field("idUser").eq(b.getInt("userID")).and().field("anonymuos").eq(0).orderBy("id", QueryOrder.Descending).execute(new TableQueryCallback<Report>() {
				@Override
				public void onCompleted(final List<Report> reports, int i, Exception e, ServiceFilterResponse response) {
					load(e, reports);
				}
			});
		} else {
		    mClient.getTable(Report.class).orderBy("id", QueryOrder.Descending).execute(new TableQueryCallback<Report>() {
				@Override
				public void onCompleted(final List<Report> reports, int i, Exception e, ServiceFilterResponse response) {
					load(e, reports);
				}
			});
		}
	}
	
	private void load(Exception e, final List<Report> reports) {
		ListView list = (ListView) findViewById(R.id.list);
		ProgressBar loading = (ProgressBar) findViewById(R.id.loading);
		TextView msg = (TextView) findViewById(R.id.msg);
		
		loading.setVisibility(View.GONE);
		if(e == null && reports.size() > 0) {
			list.setAdapter(new ListReportItemAdapter(AllReportsActivity.this, reports));
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
					Intent main = new Intent(AllReportsActivity.this, ReportActivity.class);
					Bundle b = new Bundle();
					b.putSerializable("report", reports.get(position));
					main.putExtras(b);
					
					startActivity(main);
                }
			});
			
			list.setVisibility(View.VISIBLE);
		} else {
			list.setVisibility(View.GONE);
			msg.setVisibility(View.VISIBLE);
			msg.setText("Nenhuma denúncia encontrada.");
		}
	}

    @Override
    public boolean onNavigateUp() {
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(nextScreen);

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(nextScreen);
    }
}
