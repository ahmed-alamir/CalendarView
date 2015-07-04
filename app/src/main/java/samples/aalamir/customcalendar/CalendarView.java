package samples.aalamir.customcalendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by a7med on 28/06/2015.
 */
public class CalendarView extends LinearLayout
{
	// for logging
	private static final String LOGTAG = "Calendar View";

	// how many days to show, defaults to six weeks, 42 days
	private static final int DAYS_COUNT = 42;

	// current displayed month
	private Calendar currentDate = Calendar.getInstance();

	// internal components
	private ImageView btnPrev;
	private ImageView btnNext;
	private TextView txtDate;
	private GridView grid;

	public CalendarView(Context context)
	{
		super(context);
		initControl(context);
	}

	public CalendarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initControl(context);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initControl(context);
	}

	/**
	 * Load control xml layout
	 */
	private void initControl(Context context)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.control_calendar, this);

		assignUiElements();
		assignClickHandlers();

		updateCalendar();
	}

	private void assignUiElements()
	{
		// layout is inflated, assign local variables to components
		btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
		btnNext = (ImageView)findViewById(R.id.calendar_next_button);
		txtDate = (TextView)findViewById(R.id.calendar_date_display);
		grid = (GridView)findViewById(R.id.calendar_grid);
	}

	private void assignClickHandlers()
	{
		// add one month and refresh UI
		btnNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				currentDate.add(Calendar.MONTH, 1);
				updateCalendar();
			}
		});

		// subtract one month and refresh UI
		btnPrev.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				currentDate.add(Calendar.MONTH, -1);
				updateCalendar();
			}
		});
	}

	/**
	 * Display dates correctly in grid
	 */
	public void updateCalendar()
	{
		ArrayList<Date> cells = new ArrayList<>();
		Calendar calendar = (Calendar)currentDate.clone();

		// determine the cell for current month's beginning
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		// move calendar backwards to the beginning of the week
		calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

		// fill cells
		while (cells.size() < DAYS_COUNT)
		{
			cells.add(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		// update grid
		grid.setAdapter(new CalendarAdapter(getContext(), cells, null));

		// update title
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		txtDate.setText(sdf.format(currentDate.getTime()));
	}

	private class CalendarAdapter extends ArrayAdapter<Date>
	{
		// days with events
		private HashSet<Date> eventDays;

		// for view inflation
		private LayoutInflater inflater;

		public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
		{
			super(context, R.layout.control_calendar_day, days);
			this.eventDays = eventDays;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			// day in question
			Date date = getItem(position);
			int day = date.getDate();
			int month = date.getMonth();
			int year = date.getYear();

			// today
			Date today = new Date();

			// inflate item if it does not exist yet
			if (view == null)
				view = inflater.inflate(R.layout.control_calendar_day, parent, false);

			// if this day has an event, specify event image
			view.setBackgroundResource(0);
			if (eventDays != null)
			{
				for (Date eventDate : eventDays)
				{
					if (eventDate.getDate() == day &&
							eventDate.getMonth() == month &&
							eventDate.getYear() == year)
					{
						// mark this day for event
						view.setBackgroundResource(R.drawable.reminder);
						break;
					}
				}
			}

			// clear styling
			((TextView)view).setTypeface(null, Typeface.NORMAL);
			((TextView)view).setTextColor(Color.BLACK);

			if (month != today.getMonth() || year != today.getYear())
			{
				// if this day is outside current month, grey it out
				((TextView)view).setTextColor(getResources().getColor(R.color.greyed_out));
			}
			else if (day == today.getDate())
			{
				// if it is today, set it to blue/bold
				((TextView)view).setTypeface(null, Typeface.BOLD);
				((TextView)view).setTextColor(getResources().getColor(R.color.today));
			}

			// set text
			((TextView)view).setText(String.valueOf(date.getDate()));

			return view;
		}
	}
}
