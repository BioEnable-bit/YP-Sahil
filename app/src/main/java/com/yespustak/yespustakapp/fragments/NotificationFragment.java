package com.yespustak.yespustakapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.adapters.NotificationAdapter;
import com.yespustak.yespustakapp.models.NotificationModel;
import com.yespustak.yespustakapp.utils.LoggerClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class NotificationFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    NotificationAdapter adapter;
    ArrayList<NotificationModel> arrayList;
    String TAG = this.getClass().getSimpleName();

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        assignView();
        return view;
    }

    private void assignView() {
        recyclerView = view.findViewById(R.id.rvNotification);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        arrayList = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), arrayList);

        recyclerView.setAdapter(adapter);
        setData();
    }

    private void setData() {
        String notificationJson = "[" +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/5fa2dd/ffffff\"," +
                "    \"title\": \"Community Outreach Specialist\"," +
                "    \"desc\": \"Poisoning by other psychostimulants\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/dddddd/000000\"," +
                "    \"title\": \"Pharmacist\"," +
                "    \"desc\": \"Suicide and self-inflicted injuries by jumping from residential premises\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/dddddd/000000\"," +
                "    \"title\": \"Sales Associate\"," +
                "    \"desc\": \"Anticoagulant antagonists and other coagulants causing adverse effects in therapeutic use\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/cc0000/ffffff\"," +
                "    \"title\": \"Senior Cost Accountant\"," +
                "    \"desc\": \"Other organic sleep disorders\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.jpg/ff4444/ffffff\"," +
                "    \"title\": \"Cost Accountant\"," +
                "    \"desc\": \"Ankylosis of joint, other specified sites\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.jpg/dddddd/000000\"," +
                "    \"title\": \"Tax Accountant\"," +
                "    \"desc\": \"Venereal disease, unspecified\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/ff4444/ffffff\"," +
                "    \"title\": \"Account Representative IV\"," +
                "    \"desc\": \"Convalescence following psychotherapy and other treatment for mental disorder\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/cc0000/ffffff\"," +
                "    \"title\": \"Community Outreach Specialist\"," +
                "    \"desc\": \"Conduct disorder, adolescent onset type\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.png/dddddd/000000\"," +
                "    \"title\": \"Data Coordiator\"," +
                "    \"desc\": \"Tuberculoma of brain, tubercle bacilli not found (in sputum) by microscopy, but found by bacterial culture\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.jpg/cc0000/ffffff\"," +
                "    \"title\": \"Account Representative II\"," +
                "    \"desc\": \"Abdominal tenderness, left lower quadrant\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/cc0000/ffffff\"," +
                "    \"title\": \"Health Coach IV\"," +
                "    \"desc\": \"Loss of teeth due to periodontal disease\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.jpg/dddddd/000000\"," +
                "    \"title\": \"Electrical Engineer\"," +
                "    \"desc\": \"Paranoid type schizophrenia, in remission\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.jpg/cc0000/ffffff\"," +
                "    \"title\": \"Internal Auditor\"," +
                "    \"desc\": \"Appendicitis, unqualified\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/dddddd/000000\"," +
                "    \"title\": \"Engineer I\"," +
                "    \"desc\": \"Unspecified prophylactic or treatment measure\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.png/dddddd/000000\"," +
                "    \"title\": \"Administrative Assistant I\"," +
                "    \"desc\": \"Malignant neoplasm of hypopharynx, unspecified site\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/5fa2dd/ffffff\"," +
                "    \"title\": \"Software Engineer III\"," +
                "    \"desc\": \"Fall from skateboard\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.jpg/ff4444/ffffff\"," +
                "    \"title\": \"Senior Quality Engineer\"," +
                "    \"desc\": \"Chronic cholecystitis\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.bmp/5fa2dd/ffffff\"," +
                "    \"title\": \"Civil Engineer\"," +
                "    \"desc\": \"Endodontic underfill\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.jpg/cc0000/ffffff\"," +
                "    \"title\": \"Web Developer II\"," +
                "    \"desc\": \"Prickly heat\"" +
                "  }," +
                "  {" +
                "    \"img\": \"http://dummyimage.com/100x100.jpg/cc0000/ffffff\"," +
                "    \"title\": \"Programmer Analyst IV\"," +
                "    \"desc\": \"Benign essential hypertension complicating pregnancy, childbirth, and the puerperium, antepartum condition or complication\"" +
                "  }" +
                "]";

        try {
            JSONArray notificationList = new JSONArray(notificationJson);
            if (notificationList.length() > 0) {
                for (int j = 0; j <= notificationList.length(); j++) {
                    JSONObject b = notificationList.getJSONObject(j);
                    NotificationModel notificationModel = new NotificationModel(
                            b.getString("img"),
                            b.getString("title"),
                            b.getString("desc")
                    );
                    arrayList.add(notificationModel);
                }
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
//                utils.showToast(String.valueOf(e));
            LoggerClass.error(TAG, String.valueOf(e));
        }
    }
}