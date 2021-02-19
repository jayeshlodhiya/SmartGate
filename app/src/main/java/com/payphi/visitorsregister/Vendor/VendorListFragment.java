package com.payphi.visitorsregister.Vendor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.adapters.VendorAdapter;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Vendors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VendorListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VendorListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VendorListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private VendorAdapter adapter;
    private RecyclerView recyclerView;
    private Paint paint = new Paint();
    String flag = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference docRef;
    public static VendorListFragment vendorListFragment;
    SharedPreferences sharedPreferences;
    Date d1;
    Date d2;
    public static int docSize;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Vendors> vendorsList;

    private OnFragmentInteractionListener mListener;
    String socityCode = "";
    User user;
    public ProgressDialog paydialog; // this = YourActivity

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_vendor_list, container, false);
        vendorsList = new ArrayList<>();

        adapter = new VendorAdapter(getContext(), vendorsList, adapter);

        vendorListFragment = this;
        flag = "P";
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);

        //GetBookingsDetails();
        recyclerView = (RecyclerView) view.findViewById(R.id.vendor_recycler_view);
        // swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.cswiperefresh);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //    recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        sharedPreferences = getContext().getSharedPreferences("sosessionPref", Context.MODE_PRIVATE);
        socityCode = sharedPreferences.getString("SOC_CODE", null);
        docRef = db.collection(socityCode).document("Vendors").collection("SVendors");
        GetUserData();
        LoadVendorList();

        //initSwipe();

        EditText searchField = (EditText) view.findViewById(R.id.vendorsearchboxid);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
        return view;
    }

    void filter(String text) {

        List<Vendors> temp = new ArrayList();
        for (Vendors d : vendorsList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            String name = d.getName().toLowerCase();


            text = text.toLowerCase();
            if (name.contains(text)) {
                // Toast.makeText(getContext(),d.getShopName(),Toast.LENGTH_SHORT).show();
                temp.add(d);

                adapter.updateList(temp);
                adapter.notifyDataSetChanged();
            }
        }
        //update recyclerview

    }

    private void GetUserData() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!flag.equals("P")) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();


                if (direction == ItemTouchHelper.LEFT) {
                    //adapter.removeItem(position);
                    //Toast.makeText(getApplicationContext(),"Left",Toast.LENGTH_LONG).show();


                } else if (direction == ItemTouchHelper.RIGHT) {
                    InsertVisitorToIn(position);
                    //   removeView();
                    // edit_position = position;
                    //alertDialog.setTitle("Edit Country");
                    //et_country.setText(countries.get(position));
                    // alertDialog.show();


                    //Toast.makeText(getApplicationContext(),"Right",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        paint.setColor(Color.parseColor("#00897b"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.visitorout);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    } else {
                        paint.setColor(Color.parseColor("#00897b"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.visitorout);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void InsertVisitorToIn(int position) {

        Vendors vendorObj = null;
        System.out.println("Pos==" + position);
        vendorObj = vendorsList.get(position);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        Toast.makeText(getContext(), "IN", Toast.LENGTH_LONG).show();
        /*if((vendorObj.getVistorOutTime()==null || visitorObj.getVistorOutTime().equals("")) && (user.getRole().equals("S")) ) {

            DocumentReference bookingref = FirebaseFirestore.getInstance().collection(socityCode).document("Visitors").collection("SVisitors").document(visitorObj.getDocId());
            bookingref.update("VistorOutTime", String.valueOf(dateFormat.format(date))).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("DeviceId Updated");
                }
            });

        }*/
        adapter.notifyDataSetChanged();
    }

    private void LoadVendorList() {
        if (user.getRole().equals("R")) {
            docRef.whereEqualTo("FlatNo", user.getFlatNo()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    docSize = size;
                    System.out.println("Documents size====" + size);
                    //System.out.println("bookingsList size="+visitorsList.size());
                    if (vendorsList.size() > 0) {

                        vendorsList.removeAll(vendorsList);

                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                        adapter.notifyDataSetChanged();
*/


                    }
                    if (size == 0) {
                        vendorsList.removeAll(vendorsList);
                        adapter.notifyDataSetChanged();
                    }


                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //  System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        CreateVendorList(documentSnapshot);
                    }
                }
            });
        } else {
            docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();
                    docSize = size;
                    System.out.println("Documents size====" + size);
                    //System.out.println("bookingsList size="+visitorsList.size());
                    if (vendorsList.size() > 0) {

                        vendorsList.removeAll(vendorsList);

                       /* adapter = new BookingAdapter(getApplicationContext(),bookingsList);
                        adapter.notifyDataSetChanged();
*/


                    }
                    if (size == 0) {
                        vendorsList.removeAll(vendorsList);
                        adapter.notifyDataSetChanged();
                    }


                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //  System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        CreateVendorList(documentSnapshot);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    //     Toast.makeText(getContext(),"Refreshed",Toast.LENGTH_LONG).show();
                    /*String tag = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
                    Fragment frg = getFragmentManager().findFragmentByTag(tag);
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();*/
                    //ThirdFragment thirdFragment;
                   /* Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(currentFragment);
                    fragmentTransaction.attach(currentFragment);
                    fragmentTransaction.commit();*/

                    Refresh();

                }
            });
        }
    }

    private void Refresh() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void CreateVendorList(DocumentSnapshot documentSnapshot) {

        Vendors vendors = new Vendors();
        vendors.setName(documentSnapshot.getString("Name"));
        vendors.setPic(documentSnapshot.getString("Pic"));
        vendors.setFlatNo(documentSnapshot.getString("Number"));
        vendors.setMobile(documentSnapshot.getString("Mobile"));
        vendors.setDocId(documentSnapshot.getString("DocId"));
        vendors.setActive(documentSnapshot.getString("Active"));

        //   visitor.setBookingTime(documentSnapshot.getString("Time"));
       /* bookings.setStatus(documentSnapshot.getString("Status"));
        bookings.setDocumentId(documentSnapshot.getString("DocId"));*/

        vendorsList.add(vendors);

      /*  Collections.sort(visitorsList, new Comparator<Visitor>(){
            public int compare(Visitor v1, Visitor v2) {
                //  return s1.getDistance().compareToIgnoreCase(s2.getDistance());
                //return Double.compare(Double.parseDouble(s1.getDistance()), Double.parseDouble(s2.getDistance()));
                try {
                    d1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).parse(v1.getVistorInTime());
                    d2=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).parse(v2.getVistorInTime());

                }catch (Exception e){

                }
                // return Double.compare(), v1.getVistorInTime());
                return d2.compareTo(d1);
            }
        });*/

        adapter.notifyDataSetChanged();

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position

            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public VendorListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitorsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VendorListFragment newInstance(String param1, String param2) {
        VendorListFragment fragment = new VendorListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
}
