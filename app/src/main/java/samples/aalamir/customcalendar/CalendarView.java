package samples.aalamir.customcalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

		if (isInEditMode())
		{
			ArrayList<Integer> days = new ArrayList<>();
			days.add(31);
			for (int i = 1; i <= 30; ++i)
				days.add(i);


			((GridView)findViewById(R.id.calendar_grid)).setSelection(29);
			((TextView)findViewById(R.id.calendar_date_display)).setText("JUN 2015");
		}
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();

		// layout is inflated, assign local variables to components
		btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
		btnNext = (ImageView)findViewById(R.id.calendar_next_button);
		txtDate = (TextView)findViewById(R.id.calendar_date_display);
		grid = (GridView)findViewById(R.id.calendar_grid);
	}

	/**
	 * Display dates correctly in grid
	 */
	private void updateCalendar()
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
		((CalendarAdapter)grid.getAdapter()).updateData(cells);

		// update title
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		txtDate.setText(sdf.format(currentDate.getTime()));
	}
}
