package ui;

import generator.CompleteRandomContextGenerator;

import com.passchange.passchangeapp.R;

import account.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class ChangePasswordWindow implements OnClickListener,
		OnCheckedChangeListener {

	private Account account;
	private MainFragmentActivity mainActivity;
	private CheckBox checkBoxGenerator;
	private TableRow tableRowGen, tableRowGen1;
	private Button generatorButton, submit;
	private CompleteRandomContextGenerator generator;
	private EditText lengthEditText, pass;

	public ChangePasswordWindow(Account account, MainFragmentActivity mainActivity) {
		this.account = account;
		this.mainActivity = mainActivity;

		pass = (EditText) mainActivity.findViewById(R.id.editNewPass);
		generator = new CompleteRandomContextGenerator();

		generatorButton = (Button) mainActivity
				.findViewById(R.id.buttonPassGenerate);
		generatorButton.setOnClickListener(this);
		lengthEditText = (EditText) mainActivity
				.findViewById(R.id.editNewPassLength);

		checkBoxGenerator = (CheckBox) mainActivity
				.findViewById(R.id.checkPasswordGenerator);
		checkBoxGenerator.setOnCheckedChangeListener(this);

		tableRowGen = (TableRow) mainActivity.findViewById(R.id.rowGenerator);
		tableRowGen1 = (TableRow) mainActivity.findViewById(R.id.rowGenerator1);

		ImageView imageView = (ImageView) mainActivity
				.findViewById(R.id.imageViewIcon);
		imageView.setImageResource(account.getWebsite().getImageSource());
		TextView textView = (TextView) mainActivity
				.findViewById(R.id.textViewInformation);
		String source = mainActivity.getResources().getString(R.string.about_change)+"<br>"
				+ System.getProperty("line.separator") + mainActivity.getResources().getString(R.string.of_account)+": <br>"
				+ System.getProperty("line.separator") + "<b>"
				+ account.getUserName() + "</b>"+mainActivity.getResources().getString(R.string.at) +"<b>"
				+ account.getWebsite().getName() + "</b>.";
		textView.setText(Html.fromHtml(source));
		submit = (Button) mainActivity.findViewById(R.id.buttonChangeSubmit);
		submit.setOnClickListener(this);

	}

	

	@Override
	public void onClick(View v) {
		if (v.equals(submit)) {
			if (account.getWebsite()
					.validatePassword(pass.getText().toString())) {
				account.changePassword(pass.getText().toString(), mainActivity);

				// Hide Keyboard
				InputMethodManager im = (InputMethodManager) mainActivity
						.getApplicationContext().getSystemService(
								MainActivity.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(mainActivity.getWindow()
						.getDecorView().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				mainActivity.setContentView(R.layout.activity_main);
				mainActivity.dataSetChanged();
			} else {
				new AlertDialog.Builder(mainActivity)
						.setMessage(
								mainActivity.getResources().getString(R.string.please_fulfill_condtion)
										+ account.getWebsite()
												.getPasswordCondition())
						.setTitle(mainActivity.getResources().getString(R.string.error))
						.setCancelable(true)
						.setNeutralButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								}).show();
			}
		} else if (v.equals(generatorButton)) {
			int length = 0;
			try {
				length = Integer.parseInt(lengthEditText.getText().toString());
			} catch (Exception e) {

			}
			if (length > 0) {
				pass.setInputType(EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
				pass.setText(generator.generatePassword(length));
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (checkBoxGenerator.isChecked()) {
			tableRowGen.setVisibility(View.VISIBLE);
			tableRowGen1.setVisibility(View.VISIBLE);
			lengthEditText.setText("10");
		} else {
			tableRowGen.setVisibility(View.INVISIBLE);
			tableRowGen1.setVisibility(View.INVISIBLE);
			pass.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		}

	}
}
