package com.example.ringo_star;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ringo_star.data.DatabaseClient;
import com.example.ringo_star.data.entity.User;
import com.example.ringo_star.enumeration.BloodGroup;
import com.example.ringo_star.enumeration.Gender;
import com.example.ringo_star.utils.PinUtils;
import com.example.ringo_star.utils.RingoStarRD4F;
import com.kevalpatel2106.rulerpicker.RulerValuePicker;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.Values;

public class UserInfoActivity extends AppCompatActivity {
    User userStart;
    Vibrator vibrator;

    private final String[] requiredPermissions = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_user_info);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        NumberPicker dayPicker = findViewById(R.id.dayPicker);
        NumberPicker monthPicker = findViewById(R.id.monthPicker);
        NumberPicker yearPicker = findViewById(R.id.yearPicker);

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(2100);
        yearPicker.setValue(currentYear);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(currentMonth);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(getDaysInMonth(currentYear, currentMonth));
        dayPicker.setValue(currentDay);

        dayPicker.setWrapSelectorWheel(true);
        monthPicker.setWrapSelectorWheel(true);
        yearPicker.setWrapSelectorWheel(true);

        NumberPicker.OnValueChangeListener listener = (picker, oldVal, newVal) -> {
            int selectedYear = yearPicker.getValue();
            int selectedMonth = monthPicker.getValue();
            int selectedDay = dayPicker.getValue();

            int maxDays = getDaysInMonth(selectedYear, selectedMonth);
            dayPicker.setMaxValue(maxDays);

            if(selectedDay > maxDays) {
                dayPicker.setValue(maxDays);
            }
        };

        yearPicker.setOnValueChangedListener(listener);
        monthPicker.setOnValueChangedListener(listener);
        dayPicker.setOnValueChangedListener(listener);

        Spinner bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.bloodGroupSpinner,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bloodGroupSpinner.setAdapter(adapter);

        RulerValuePicker rulerPickerHeight = findViewById(R.id.rulerPickerHeight);
        RulerValuePicker rulerPickerWeight = findViewById(R.id.rulerPickerWeight);

        rulerPickerHeight.selectValue(180);
        rulerPickerWeight.selectValue(80);

        EditText txtFirstname = findViewById(R.id.txtFirstname);
        EditText txtLastname = findViewById(R.id.txtLastname);
        EditText txtPIN = findViewById(R.id.txtPIN);

        RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);

        CheckBox smoke = findViewById(R.id.smoke);

        Button btnOk = findViewById(R.id.btnOk);

        new Thread(() -> {
            DatabaseClient databaseClient = DatabaseClient.getInstance(getApplicationContext());
            userStart = databaseClient.getDatabase().userDAO().getUser();

            if(userStart != null) {
                Gender gender = userStart.getGender();
                LocalDate birthday = userStart.getBirthday();
                BloodGroup bloodGroup = userStart.getBloodGroup();
                boolean isSmoke = userStart.isSmoke();

                txtFirstname.setText(userStart.getFirstname());
                txtLastname.setText(userStart.getLastname());

                if(gender == Gender.MALE)
                    radioGroupGender.check(R.id.radioButtonMale);
                else
                    radioGroupGender.check(R.id.radioButtonFemale);

                int dayBirthday = birthday.getDayOfMonth();
                int monthBirthday = birthday.getMonthValue();
                int yearBirthday = birthday.getYear();

                dayPicker.setValue(dayBirthday);
                monthPicker.setValue(monthBirthday);
                yearPicker.setValue(yearBirthday);

                rulerPickerHeight.selectValue(userStart.getHeight());
                rulerPickerWeight.selectValue(userStart.getWeight());

                if(bloodGroup == BloodGroup.A_positive)
                    bloodGroupSpinner.setSelection(0);
                else if(bloodGroup == BloodGroup.A_negative)
                    bloodGroupSpinner.setSelection(1);
                else if(bloodGroup == BloodGroup.B_positive)
                    bloodGroupSpinner.setSelection(2);
                else if(bloodGroup == BloodGroup.B_negative)
                    bloodGroupSpinner.setSelection(3);
                else if(bloodGroup == BloodGroup.AB_positive)
                    bloodGroupSpinner.setSelection(4);
                else if(bloodGroup == BloodGroup.AB_negative)
                    bloodGroupSpinner.setSelection(5);
                else if(bloodGroup == BloodGroup.zero_positive)
                    bloodGroupSpinner.setSelection(6);
                else if(bloodGroup == BloodGroup.zero_negative)
                    bloodGroupSpinner.setSelection(7);

                smoke.setChecked(isSmoke);
            }
        }).start();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = txtFirstname.getText().toString();
                String lastname = txtLastname.getText().toString();
                String PIN = txtPIN.getText().toString();

                int selectedId = radioGroupGender.getCheckedRadioButtonId();

                RadioButton selectedRadioButton = findViewById(selectedId);
                Gender gender = Gender.valueOf(selectedRadioButton.getText().toString().toUpperCase());

                int selectedDay = dayPicker.getValue();
                int selectedMonth = monthPicker.getValue();
                int selectedYear = yearPicker.getValue();

                int height = rulerPickerHeight.getCurrentValue();
                int weight = rulerPickerWeight.getCurrentValue();

                String bloodGroupString = bloodGroupSpinner.getSelectedItem().toString();

                BloodGroup bloodGroup = null;

                switch(bloodGroupString) {
                    case "A+":
                        bloodGroup = BloodGroup.A_positive;
                        break;

                    case "A-":
                        bloodGroup = BloodGroup.A_negative;
                        break;

                    case "B+":
                        bloodGroup = BloodGroup.B_positive;
                        break;

                    case "B-":
                        bloodGroup = BloodGroup.B_negative;
                        break;

                    case "AB+":
                        bloodGroup = BloodGroup.AB_positive;
                        break;

                    case "AB-":
                        bloodGroup = BloodGroup.AB_negative;
                        break;

                    case "0+":
                        bloodGroup = BloodGroup.zero_positive;
                        break;

                    case "0-":
                        bloodGroup = BloodGroup.zero_negative;
                        break;
                }

                boolean smoker = smoke.isChecked();

                if(firstname.isEmpty() || lastname.isEmpty() || PIN.isEmpty()) {
                    if(vibrator.hasVibrator()) {
                        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(vibrationEffect);
                    }

                    ConstraintLayout main = findViewById(R.id.main);

                    Animation shake = AnimationUtils.loadAnimation(UserInfoActivity.this, R.anim.shake);
                    main.startAnimation(shake);

                    Toast.makeText(getApplicationContext(), R.string.toast_all_fields_requires, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        byte[] salt = PinUtils.generateSalt();
                        String hashedPin = PinUtils.hashPin(PIN, salt);

                        User user = new User();

                        user.setFirstname(firstname);
                        user.setLastname(lastname);

                        user.setSalt(salt);
                        user.setHashedPin(hashedPin);

                        user.setGender(gender);
                        user.setBirthday(LocalDate.of(selectedYear, selectedMonth, selectedDay));
                        user.setHeight(height);
                        user.setWeight(weight);
                        user.setBloodGroup(bloodGroup);
                        user.setSmoke(smoker);

                        LocalDate today = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        String formattedToday = today.format(formatter);

                        if(RingoStarRD4F.fileExists(getApplicationContext(), "kg.ttl")) {
                            Model model = RingoStarRD4F.loadModelFromFile(getApplicationContext(), "kg.ttl");

                            model.forEach(System.out::println);

                            Optional<String> lastSmokeRecordNode = RingoStarRD4F.getLiteralValue(model, RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastSmokeRecordIRI);
                            Optional<String> lastWeightRecordNode = RingoStarRD4F.getLiteralValue(model, RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastWeightRecordIRI);

                            lastSmokeRecordNode.ifPresentOrElse(
                                value -> {
                                    Optional<String> lastSmokeRecordNodeValue = RingoStarRD4F.getLiteralValue(model, Values.iri(RingoStarRD4F.nodeNS, value), RingoStarRD4F.propertyValue);

                                    lastSmokeRecordNodeValue.ifPresentOrElse(
                                        value1 -> {
                                            if(!String.valueOf(smoker).equals(value1)) {
                                                UUID randomUUID = UUID.randomUUID();

                                                IRI nodeSmokeRecordIRI = Values.iri(RingoStarRD4F.nodeNS, "smokeRecord" + randomUUID);
                                                model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.relationCarry, nodeSmokeRecordIRI);
                                                model.remove(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastSmokeRecordIRI, null);
                                                model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastSmokeRecordIRI, Values.literal("smokeRecord" + randomUUID));
                                                model.add(nodeSmokeRecordIRI, RingoStarRD4F.propertyValue, Values.literal(smoker));
                                                model.add(nodeSmokeRecordIRI, RingoStarRD4F.propertyDate, Values.literal(formattedToday));
                                            }
                                        },
                                        () -> {}
                                    );
                                },
                                () -> {}
                            );

                            lastWeightRecordNode.ifPresentOrElse(
                                value -> {
                                    Optional<String> lastWeightRecordNodeValue = RingoStarRD4F.getLiteralValue(model, Values.iri(RingoStarRD4F.nodeNS, value), RingoStarRD4F.propertyValue);

                                    lastWeightRecordNodeValue.ifPresentOrElse(
                                        value1 -> {
                                            if(!String.valueOf(smoker).equals(value1)) {
                                                UUID randomUUID = UUID.randomUUID();

                                                IRI nodeWeightRecordIRI = Values.iri(RingoStarRD4F.nodeNS, "weightRecord" + randomUUID);
                                                model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.relationCarry, nodeWeightRecordIRI);
                                                model.remove(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastWeightRecordIRI, null);
                                                model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastWeightRecordIRI, Values.literal("weightRecord" + randomUUID));
                                                model.add(nodeWeightRecordIRI, RingoStarRD4F.propertyValue, Values.literal(weight));
                                                model.add(nodeWeightRecordIRI, RingoStarRD4F.propertyDate, Values.literal(formattedToday));
                                            }
                                        },
                                        () -> {}
                                    );
                                },
                                () -> {}
                            );

                            RingoStarRD4F.saveModelToFile(model, getApplicationContext(), "kg.ttl");
                        } else {
                            Model model = new LinkedHashModel();

                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyFirstnameIRI, Values.literal(firstname));
                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastnameIRI, Values.literal(lastname));
                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyGenderIRI, Values.literal(gender));
                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyBirthdayIRI, Values.literal(LocalDate.of(selectedYear, selectedMonth, selectedDay)));
                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyHeightIRI, Values.literal(height));
                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyBloodGroupIRI, Values.literal(bloodGroup));

                            UUID randomUUID = UUID.randomUUID();

                            IRI nodeSmokeRecordIRI = Values.iri(RingoStarRD4F.nodeNS, "smokeRecord" + randomUUID);
                            IRI nodeWeightRecordIRI = Values.iri(RingoStarRD4F.nodeNS, "weightRecord" + randomUUID);

                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.relationCarry, nodeSmokeRecordIRI);
                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.relationCarry, nodeWeightRecordIRI);

                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastSmokeRecordIRI, Values.literal("smokeRecord" + randomUUID));
                            model.add(RingoStarRD4F.nodeUserIRI, RingoStarRD4F.propertyLastWeightRecordIRI, Values.literal("weightRecord" + randomUUID));

                            model.add(nodeSmokeRecordIRI, RingoStarRD4F.propertyValue, Values.literal(smoker));
                            model.add(nodeSmokeRecordIRI, RingoStarRD4F.propertyDate, Values.literal(formattedToday));

                            model.add(nodeWeightRecordIRI, RingoStarRD4F.propertyValue, Values.literal(weight));
                            model.add(nodeWeightRecordIRI, RingoStarRD4F.propertyDate, Values.literal(formattedToday));

                            RingoStarRD4F.saveModelToFile(model, getApplicationContext(), "kg.ttl");
                        }

                        new Thread(() -> {
                            DatabaseClient databaseClient = DatabaseClient.getInstance(getApplicationContext());

                            if(userStart == null)
                                databaseClient.getDatabase().userDAO().insert(user);
                            else {
                                user.setId(userStart.getId());
                                databaseClient.getDatabase().userDAO().update(user);
                            }

                            startActivity(new Intent(UserInfoActivity.this, HomeActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }).start();
                    } catch(Exception ignored) {}
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        managePermissions();
    }

    private void managePermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        for(String permission : requiredPermissions) {
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if(!permissionsToRequest.isEmpty()) {
            int PERMISSION_REQUEST_CODE = 100;
            requestPermissions(permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    private int getDaysInMonth(int year, int month) {
        switch(month) {
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;

            case 2:
                return (isLeapYear(year)) ? 29 : 28;

            default:
                return 31;
        }
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}