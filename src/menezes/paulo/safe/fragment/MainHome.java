package menezes.paulo.safe.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import menezes.paulo.safe.AllReportsActivity;
import menezes.paulo.safe.R;
import menezes.paulo.safe.ReportActivity;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Report;
import menezes.paulo.safe.entity.Report_Photo;
import menezes.paulo.safe.task.ImageDownloaderTask;
import menezes.dd.processbutton.FlatButton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MainHome extends Fragment {
	private View rootView;
	private MobileServiceClient mClient;
	
	private ProgressBar mMainLoading;
	private LinearLayout mReport1, mReport2;
	private TableLayout mReportMiddle;
	private TextView mNoResults;
	private FlatButton mMoreReports;
	
	private Context mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		if (getActivity() != null)
			mContext = getActivity();
		
		mClient = Connect.getInstance();
		
		setHasOptionsMenu(true);
		
		mMainLoading = (ProgressBar) rootView.findViewById(R.id.mainLoading);
		mNoResults = (TextView) rootView.findViewById(R.id.noResults);
		mReport1 = (LinearLayout) rootView.findViewById(R.id.report1);
		mReport2 = (LinearLayout) rootView.findViewById(R.id.report2);
		mReportMiddle = (TableLayout) rootView.findViewById(R.id.reportMiddle);
		mMoreReports = (FlatButton) rootView.findViewById(R.id.moreReports);
		
		mClient.getTable(Report.class).orderBy("id", QueryOrder.Descending).execute(new TableQueryCallback<Report>() {
			@Override
			public void onCompleted(final List<Report> reports, int i, Exception e, ServiceFilterResponse reponse) {
				if(e == null) {
					if(reports.size() > 0) {
						mReport1.setVisibility(View.VISIBLE);
						((TextView)mReport1.findViewById(R.id.report1Title)).setText(reports.get(0).title);
						
						if(reports.get(0).description != null && reports.get(0).description.length() > 0) {
							((TextView)mReport1.findViewById(R.id.report1Description)).setVisibility(View.VISIBLE);
							((TextView)mReport1.findViewById(R.id.report1Description)).setText(reports.get(0).description);
						}
						
						loadImage(reports.get(0).id, ((ImageView)mReport1.findViewById(R.id.report1Image)));
						
						mReport1.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent next = new Intent(mContext, ReportActivity.class);
								Bundle b = new Bundle();
								b.putSerializable("report", reports.get(0));
								next.putExtras(b);
								
								mContext.startActivity(next);
							}
						});

						if(reports.size() > 1) {
							mReport2.setVisibility(View.VISIBLE);
							((TextView)mReport2.findViewById(R.id.report2Title)).setText(reports.get(1).title);
							
							if(reports.get(1).description != null && reports.get(1).description.length() > 0) {
								//((TextView)mReport2.findViewById(R.id.report2Description)).setVisibility(View.VISIBLE);
								//((TextView)mReport2.findViewById(R.id.report2Description)).setText(reports.get(1).description);
							}
							
							loadImage(reports.get(1).id, ((ImageView)mReport2.findViewById(R.id.report2Image)));
							
							mReport2.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									Intent next = new Intent(mContext, ReportActivity.class);
									Bundle b = new Bundle();
									b.putSerializable("report", reports.get(1));
									next.putExtras(b);
									
									mContext.startActivity(next);
								}
							});
						}
						
						if(reports.size() > 4) {
							mReportMiddle.setVisibility(View.VISIBLE);
						}
						
						// Middle
						if(reports.size() > 4) {
							mReportMiddle.findViewById(R.id.reportMiddle1).setVisibility(View.VISIBLE);
							((TextView)mReportMiddle.findViewById(R.id.reportMiddle1Title)).setText(reports.get(2).title);
							if(reports.get(2).description != null && reports.get(2).description.length() > 0) {
								//((TextView)mReportMiddle.findViewById(R.id.reportMiddle1Description)).setVisibility(View.VISIBLE);
								//((TextView)mReportMiddle.findViewById(R.id.reportMiddle1Description)).setText(reports.get(2).description);
							}
							
							loadImage(reports.get(2).id, ((ImageView)mReportMiddle.findViewById(R.id.reportMiddle1Image)));
							
							mReportMiddle.findViewById(R.id.reportMiddle1).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									Intent next = new Intent(mContext, ReportActivity.class);
									Bundle b = new Bundle();
									b.putSerializable("report", reports.get(2));
									next.putExtras(b);
									
									mContext.startActivity(next);
								}
							});
						}
						if(reports.size() > 4) {
							mReportMiddle.findViewById(R.id.reportMiddle2).setVisibility(View.VISIBLE);
							((TextView)mReportMiddle.findViewById(R.id.reportMiddle2Title)).setText(reports.get(3).title);
							if(reports.get(3).description != null && reports.get(3).description.length() > 0) {
								//((TextView)mReportMiddle.findViewById(R.id.reportMiddle2Description)).setVisibility(View.VISIBLE);
								//((TextView)mReportMiddle.findViewById(R.id.reportMiddle2Description)).setText(reports.get(3).description);
							}
							
							loadImage(reports.get(3).id, ((ImageView)mReportMiddle.findViewById(R.id.reportMiddle2Image)));
							
							mReportMiddle.findViewById(R.id.reportMiddle2).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									Intent next = new Intent(mContext, ReportActivity.class);
									Bundle b = new Bundle();
									b.putSerializable("report", reports.get(3));
									next.putExtras(b);
									
									mContext.startActivity(next);
								}
							});
						}
						if(reports.size() > 4) {
							mReportMiddle.findViewById(R.id.reportMiddle3).setVisibility(View.VISIBLE);
							((TextView)mReportMiddle.findViewById(R.id.reportMiddle3Title)).setText(reports.get(4).title);
							if(reports.get(4).description != null && reports.get(4).description.length() > 0) {
								//((TextView)mReportMiddle.findViewById(R.id.reportMiddle3Description)).setVisibility(View.VISIBLE);
								//((TextView)mReportMiddle.findViewById(R.id.reportMiddle3Description)).setText(reports.get(4).description);
							}
							
							loadImage(reports.get(4).id, ((ImageView)mReportMiddle.findViewById(R.id.reportMiddle3Image)));
							
							mReportMiddle.findViewById(R.id.reportMiddle3).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									Intent next = new Intent(mContext, ReportActivity.class);
									Bundle b = new Bundle();
									b.putSerializable("report", reports.get(4));
									next.putExtras(b);
									
									mContext.startActivity(next);
								}
							});
						}
						// Middle
							
						mNoResults.setVisibility(View.GONE);
						mMainLoading.setVisibility(View.GONE);
						mMoreReports.setVisibility(View.VISIBLE);
						mMoreReports.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								Intent i = new Intent(getActivity(), AllReportsActivity.class);
								startActivity(i);
							}
						});
					} else {
						mMoreReports.setVisibility(View.GONE);
						mNoResults.setVisibility(View.VISIBLE);
						mMainLoading.setVisibility(View.GONE);
					}
				}
			}
		});
		
		return rootView;
	}
	
	private void loadImage(int id, final ImageView iv) {
		mClient.getTable(Report_Photo.class).where().field("idReport").eq(id).top(1).execute(new TableQueryCallback<Report_Photo>() {
			@Override
			public void onCompleted(List<Report_Photo> photos, int i, Exception e, ServiceFilterResponse response) {
				if(e == null) {
					if(photos.size() > 0 && photos.get(i).image != null && photos.get(i).image.length() > 0) {
						iv.setVisibility(View.VISIBLE);
						new ImageDownloaderTask(iv).execute("http://wisapp.azurewebsites.net/safe/" + photos.get(i).image);
					}
				}
			}
		});
	}
}
