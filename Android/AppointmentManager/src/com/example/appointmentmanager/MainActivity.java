package com.example.appointmentmanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.app.AlertDialog;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author
 * 
 *         main activity 1. search by name, show popup if result found, see
 *         private void searchByName(String query) 2. pick a day and show popop
 *         if that day has appointment, see private void showDaily(String query)
 *         3. pick a name on that popup to go to next page
 * 
 */
public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	// I can't handle popup type in func parameter so I placed a global var
	enum PopupType {
		Search, Daily
	}

	PopupType popupType;

	SearchView search;

	AlertDialog alertDialogStores;

	// these are for calendar, you can skip
	private Button currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	private int month, year;
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";

	private void showDaily(String query) throws IOException {

		String URL = "http://10.0.2.2:8080/Appointment/AppointmentRequest?type=getDaily&time="
				+ query;

		try {
			// see comments in class PostAsyncTast
			InputStream response = new PostAsyncTask().execute(URL).get();
			if (response == null) {
				Toast.makeText(getApplicationContext(),
						"No result match your query", Toast.LENGTH_LONG).show();
				return;
			}

			//Parses the data into the selected ID's
			
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(response);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			ObjectItem[] ObjectItemData = new ObjectItem[nodeList.getLength()];
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;

					String id = elem.getElementsByTagName("AppointmentID")
							.item(0).getChildNodes().item(0).getNodeValue();

					String patientID = elem.getElementsByTagName("PatientID")
							.item(0).getChildNodes().item(0).getNodeValue();

					String name = "";

					try {
						URL = "http://10.0.2.2:8080/Appointment/AppointmentRequest?type=getPatient&PatientID="
								+ patientID;
						InputStream response1 = new PostAsyncTask()
								.execute(URL).get();
						if (response1 == null) {
							Toast.makeText(getApplicationContext(),
									"No patient matches ID" + patientID,
									Toast.LENGTH_LONG).show();
							return;
						}
						DocumentBuilderFactory factory1 = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder1 = factory1
								.newDocumentBuilder();

						Document document1 = builder1.parse(response1);
						NodeList nodeList1 = document1.getDocumentElement()
								.getChildNodes();

						for (int i1 = 0; i1 < nodeList1.getLength(); i1++) {
							Node node1 = nodeList1.item(i1);

							if (node1.getNodeType() == Node.ELEMENT_NODE) {
								Element elem1 = (Element) node1;

								name = (elem1
										.getElementsByTagName("PatientName")
										.item(0).getChildNodes().item(0)
										.getNodeValue());

								ObjectItemData[i] = new ObjectItem(
										Integer.parseInt(id), name);
							}
						}

					} catch (SAXException | InterruptedException
							| ExecutionException | ParserConfigurationException
							| IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			// our adapter instance
			AppointmentAdapter adapter = new AppointmentAdapter(this,
					R.layout.appointment_list, ObjectItemData);

			// create a new ListView, set the adapter and item click listener
			ListView listViewItems = new ListView(this);
			listViewItems.setAdapter(adapter);
			listViewItems.setOnItemClickListener(this);

			// put the ListView in the pop up
			alertDialogStores = new AlertDialog.Builder(MainActivity.this)
					.setView(listViewItems).setTitle("Search result").show();
		} catch (SAXException | InterruptedException | ExecutionException
				| ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// same script as private void showDaily(String query
	private void searchByName(String query) throws IOException {
		String URL = "http://10.0.2.2:8080/Appointment/AppointmentRequest?type=searchByName&name="
				+ query;

		try {
			InputStream response = new PostAsyncTask().execute(URL).get();
			if (response == null) {
				Toast.makeText(getApplicationContext(),
						"No result match your query", Toast.LENGTH_LONG).show();
				return;
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(response);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			ObjectItem[] ObjectItemData = new ObjectItem[nodeList.getLength()];
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;

					String id = elem.getElementsByTagName("PatientID").item(0)
							.getChildNodes().item(0).getNodeValue();

					String name = elem.getElementsByTagName("PatientName")
							.item(0).getChildNodes().item(0).getNodeValue();

					ObjectItemData[i] = new ObjectItem(Integer.parseInt(id),
							name);
				}
			}

			// our adapter instance
			AppointmentAdapter adapter = new AppointmentAdapter(this,
					R.layout.appointment_list, ObjectItemData);

			// create a new ListView, set the adapter and item click listener
			ListView listViewItems = new ListView(this);
			listViewItems.setAdapter(adapter);
			listViewItems.setOnItemClickListener(this);

			// put the ListView in the pop up
			alertDialogStores = new AlertDialog.Builder(MainActivity.this)
					.setView(listViewItems).setTitle("Search result").show();
		} catch (SAXException | InterruptedException | ExecutionException
				| ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// handle onItemClick event of popup
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TextView textViewItem = ((TextView) arg1
				.findViewById(R.id.textViewItem));
		String listItemId = textViewItem.getTag().toString();

		if (popupType == PopupType.Search) {
			Intent intent = new Intent(MainActivity.this, PatientActivity.class);
			intent.putExtra("PatientID", listItemId);
			startActivity(intent);
			alertDialogStores.cancel();
		} else if (popupType == PopupType.Daily) {
			Intent intent = new Intent(MainActivity.this,
					AppointmentActivity.class);
			intent.putExtra("AppointmentID", listItemId);
			startActivity(intent);
		}
		alertDialogStores.cancel();

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_calendar_view);

		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH);
		year = _calendar.get(Calendar.YEAR);

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) this.findViewById(R.id.currentMonth);
		currentMonth.setText(dateFormatter.format(dateTemplate,
				_calendar.getTime()));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		// Initialise search
		adapter = new GridCellAdapter(getApplicationContext(),
				R.id.day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);

		search = (SearchView) findViewById(R.id.searchView1);
		search.setQueryHint("Search for patient");

		// setup search function
		// *** setOnQueryTextFocusChangeListener ***
		search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			}
		});

		search.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				/*
				 * Intent intent = new Intent(MainActivity.this,
				 * SearchActivity.class); intent.putExtra("query", query);
				 * startActivity(intent);
				 */
				popupType = PopupType.Search;
				try {
					searchByName(query);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.addPatient) {

			Intent intent = new Intent(MainActivity.this,
					AddPatientActivity.class);
			intent.putExtra("PatientID", "");
			intent.putExtra("PatientName", "");
			intent.putExtra("Address", "");
			intent.putExtra("Phone", "");
			intent.putExtra("Email", "");
			startActivity(intent);
			return true;
		}

		else if (id == R.id.addAppointment) {
			Intent intent = new Intent(MainActivity.this,
					AddAppointmentActivity.class);
			intent.putExtra("AppointmentID", "");
			intent.putExtra("Time", "");
			intent.putExtra("PatientID", "");
			intent.putExtra("PatientName", "");
			intent.putExtra("Address", "");
			intent.putExtra("Phone", "");
			intent.putExtra("Email", "");
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			if (month <= 1) {
				month = 11;
				year--;
			} else {
				month--;
			}
//Sets the date to actual dates isnmtead of server time
			adapter = new GridCellAdapter(getApplicationContext(),
					R.id.day_gridcell, month, year);
			_calendar.set(year, month, _calendar.get(Calendar.DAY_OF_MONTH));
			currentMonth.setText(dateFormatter.format(dateTemplate,
					_calendar.getTime()));

			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
		}
		if (v == nextMonth) {
			if (month >= 11) {
				month = 0;
				year++;
			} else {
				month++;
			}

			adapter = new GridCellAdapter(getApplicationContext(),
					R.id.day_gridcell, month, year);
			_calendar.set(year, month, _calendar.get(Calendar.DAY_OF_MONTH));
			currentMonth.setText(dateFormatter.format(dateTemplate,
					_calendar.getTime()));
			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
		}

	}

	// Inner Class
	//Custom gridadapter which is filled with dates
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;
		private final List<String> list;
		private final String[] months = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private final int month, year;
		int daysInMonth, prevMonthDays;
		private final int currentDayOfMonth;
		private Button gridcell;

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			this.month = month;
			this.year = year;

			Log.d(tag, "Month: " + month + " " + "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

			printMonth(month, year);
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		private void printMonth(int mm, int yy) {
			// The number of days to leave blank at
			// the start of this month.
			int trailingSpaces = 0;
			int leadSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			GregorianCalendar cal = new GregorianCalendar(yy, mm,
					currentDayOfMonth);

			// Days in Current Month
			daysInMonth = daysOfMonth[mm];
			int currentMonth = mm;
			if (currentMonth == 11) {
				prevMonth = 10;
				daysInPrevMonth = daysOfMonth[prevMonth];
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = daysOfMonth[prevMonth];
				nextMonth = 1;
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = daysOfMonth[prevMonth];
			}

			// Compute how much to leave before before the first day of the
			// month.
			// getDay() returns 0 for Sunday.
			trailingSpaces = cal.get(Calendar.DAY_OF_WEEK) - 1;

			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
				++daysInMonth;
			}

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				list.add(String.valueOf((daysInPrevMonth - trailingSpaces + 1)
						+ i)
						+ "-GREY" + "-" + months[prevMonth] + "-" + prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				list.add(String.valueOf(i) + "-WHITE" + "-" + months[mm] + "-"
						+ yy);
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				Log.d(tag, "NEXT MONTH:= " + months[nextMonth]);
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ months[nextMonth] + "-" + nextYear);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(tag, "getView ...");
			View row = convertView;
			if (row == null) {
				// ROW INFLATION
				Log.d(tag, "Starting XML Row Inflation ... ");
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.day_gridcell, parent, false);

				Log.d(tag, "Successfully completed XML Row Inflation!");
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.day_gridcell);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING

			String[] day_color = list.get(position).split("-");
			gridcell.setText(day_color[0]);
			gridcell.setTag(day_color[0] + "-" + day_color[2] + "-"
					+ day_color[3]);

			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(Color.LTGRAY);
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(Color.WHITE);
			}

			return row;
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();

			Toast.makeText(getApplicationContext(), date_month_year,
					Toast.LENGTH_SHORT).show();
			popupType = PopupType.Daily;
			try {
				showDaily(date_month_year);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}