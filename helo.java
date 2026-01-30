  import javax.swing.*;
  import javax.swing.table.DefaultTableModel;
  import java.awt.*;
  import java.awt.event.ActionEvent;
  import java.awt.event.ActionListener;
  import java.time.LocalDateTime;
  import java.time.format.DateTimeFormatter;
  import java.time.format.DateTimeParseException;
  import java.util.*;
  import java.util.List;

  public class HospitalManagementSystem extends JFrame {
      private static final String SPREADSHEET_ID = "YOUR_SPREADSHEET_ID_HERE"; // Replace with your Google Sheet ID

      private List<Patient> patients = new ArrayList<>();
      private Map<String, Doctor> doctors = new HashMap<>();
      private List<Appointment> appointments = new ArrayList<>();

      private JTable patientTable, doctorTable, appointmentTable;
      private DefaultTableModel patientModel, doctorModel, appointmentModel;

      public HospitalManagementSystem() {
          setTitle("Hospital Patient Management System");
          setSize(800, 600);
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          setLayout(new BorderLayout());

          // Tabs for different sections
          JTabbedPane tabbedPane = new JTabbedPane();

          // Patient Management Tab
          JPanel patientPanel = new JPanel(new BorderLayout());
          patientModel = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Contact"}, 0);
          patientTable = new JTable(patientModel);
          JScrollPane patientScroll = new JScrollPane(patientTable);
          patientPanel.add(patientScroll, BorderLayout.CENTER);

          JPanel patientButtons = new JPanel();
          JButton addPatientBtn = new JButton("Add Patient");
          JButton updatePatientBtn = new JButton("Update Patient");
          JButton deletePatientBtn = new JButton("Delete Patient");
          JButton exportPatientsBtn = new JButton("Export to Google Sheets");
          patientButtons.add(addPatientBtn);
          patientButtons.add(updatePatientBtn);
          patientButtons.add(deletePatientBtn);
          patientButtons.add(exportPatientsBtn);
          patientPanel.add(patientButtons, BorderLayout.SOUTH);

          tabbedPane.addTab("Patients", patientPanel);

          // Doctor Management Tab
          JPanel doctorPanel = new JPanel(new BorderLayout());
          doctorModel = new DefaultTableModel(new String[]{"ID", "Name", "Specialty"}, 0);
          doctorTable = new JTable(doctorModel);
          JScrollPane doctorScroll = new JScrollPane(doctorTable);
          doctorPanel.add(doctorScroll, BorderLayout.CENTER);

          JPanel doctorButtons = new JPanel();
          JButton addDoctorBtn = new JButton("Add Doctor");
          JButton updateDoctorBtn = new JButton("Update Doctor");
          JButton deleteDoctorBtn = new JButton("Delete Doctor");
          JButton exportDoctorsBtn = new JButton("Export to Google Sheets");
          doctorButtons.add(addDoctorBtn);
          doctorButtons.add(updateDoctorBtn);
          doctorButtons.add(deleteDoctorBtn);
          doctorButtons.add(exportDoctorsBtn);
          doctorPanel.add(doctorButtons, BorderLayout.SOUTH);

          tabbedPane.addTab("Doctors", doctorPanel);

          // Appointment Management Tab
          JPanel appointmentPanel = new JPanel(new BorderLayout());
          appointmentModel = new DefaultTableModel(new String[]{"ID", "Patient", "Doctor", "DateTime", "Notes"}, 0);
          appointmentTable = new JTable(appointmentModel);
          JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
          appointmentPanel.add(appointmentScroll, BorderLayout.CENTER);

          JPanel appointmentButtons = new JPanel();
          JButton bookAppointmentBtn = new JButton("Book Appointment");
          JButton cancelAppointmentBtn = new JButton("Cancel Appointment");
          JButton exportAppointmentsBtn = new JButton("Export to Google Sheets");
          appointmentButtons.add(bookAppointmentBtn);
          appointmentButtons.add(cancelAppointmentBtn);
          appointmentButtons.add(exportAppointmentsBtn);
          appointmentPanel.add(appointmentButtons, BorderLayout.SOUTH);

          tabbedPane.addTab("Appointments", appointmentPanel);

          add(tabbedPane, BorderLayout.CENTER);

          // Event Listeners
          addPatientBtn.addActionListener(e -> addPatient());
          updatePatientBtn.addActionListener(e -> updatePatient());
          deletePatientBtn.addActionListener(e -> deletePatient());
          exportPatientsBtn.addActionListener(e -> exportPatients());

          addDoctorBtn.addActionListener(e -> addDoctor());
          updateDoctorBtn.addActionListener(e -> updateDoctor());
          deleteDoctorBtn.addActionListener(e -> deleteDoctor());
          exportDoctorsBtn.addActionListener(e -> exportDoctors());

          bookAppointmentBtn.addActionListener(e -> bookAppointment());
          cancelAppointmentBtn.addActionListener(e -> cancelAppointment());
          exportAppointmentsBtn.addActionListener(e -> exportAppointments());

          refreshTables();
      }

      // Patient methods
      private void addPatient() {
          try {
              String id = JOptionPane.showInputDialog("Enter Patient ID:");
              String name = JOptionPane.showInputDialog("Enter Patient Name:");
              int age = Integer.parseInt(JOptionPane.showInputDialog("Enter Patient Age:"));
              String contact = JOptionPane.showInputDialog("Enter Patient Contact:");
              patients.add(new Patient(id, name, age, contact));
              refreshTables();
          } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(this, "Invalid age input.");
          }
      }

      private void updatePatient() {
          int row = patientTable.getSelectedRow();
          if (row >= 0) {
              Patient p = patients.get(row);
              p.setName(JOptionPane.showInputDialog("Update Name:", p.getName()));
              try {
                  p.setAge(Integer.parseInt(JOptionPane.showInputDialog("Update Age:", p.getAge())));
              } catch (NumberFormatException e) {
                  JOptionPane.showMessageDialog(this, "Invalid age.");
              }
              p.setContact(JOptionPane.showInputDialog("Update Contact:", p.getContact()));
              refreshTables();
          } else {
              JOptionPane.showMessageDialog(this, "Select a patient to update.");
          }
      }

      private void deletePatient() {
          int row = patientTable.getSelectedRow();
          if (row >= 0) {
              patients.remove(row);
              refreshTables();
          } else {
              JOptionPane.showMessageDialog(this, "Select a patient to delete.");
          }
      }

      private void exportPatients() {
          try {
              Sheets service = GoogleSheetsService.getSheetsService();
              GoogleSheetsService.exportPatients(service, SPREADSHEET_ID, patients);
              JOptionPane.showMessageDialog(this, "Patients exported to Google Sheets successfully!");
          } catch (Exception e) {
              JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage());
          }
      }

      // Doctor methods
      private void addDoctor() {
          String id = JOptionPane.showInputDialog("Enter Doctor ID:");
          String name = JOptionPane.showInputDialog("Enter Doctor Name:");
          String specialty = JOptionPane.showInputDialog("Enter Doctor Specialty:");
          doctors.put(id, new Doctor(id, name, specialty));
          refreshTables();
      }

      private void updateDoctor() {
          String id = JOptionPane.showInputDialog("Enter Doctor ID to update:");
          Doctor d = doctors.get(id);
          if (d != null) {
              d.setName(JOptionPane.showInputDialog("Update Name:", d.getName()));
              d.setSpecialty(JOptionPane.showInputDialog("Update Specialty:", d.getSpecialty()));
              refreshTables();
          } else {
              JOptionPane.showMessageDialog(this, "Doctor not found.");
          }
      }

      private void deleteDoctor() {
          String id = JOptionPane.showInputDialog("Enter Doctor ID to delete:");
          doctors.remove(id);
          refreshTables();
      }

      private void exportDoctors() {
          try {
              Sheets service = GoogleSheetsService.getSheetsService();
              GoogleSheetsService.exportDoctors(service, SPREADSHEET_ID, doctors);
              JOptionPane.showMessageDialog(this, "Doctors exported to Google Sheets successfully!");
          } catch (Exception e) {
              JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage());
          }
      }

      // Appointment methods
      private void bookAppointment() {
          try {
              String id = JOptionPane.showInputDialog("Enter Appointment ID:");
              String patientId = JOptionPane.showInputDialog("Enter Patient ID:");
              String doctorId = JOptionPane.showInputDialog("Enter Doctor ID:");
              String dateTimeStr = JOptionPane.showInputDialog("Enter DateTime (yyyy-MM-dd HH:mm):");
              LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
              String notes = JOptionPane.showInputDialog("Enter Notes (e.g., prescription):");

              Patient patient = patients.stream().filter(p -> p.getId().equals(patientId)).findFirst().orElse(null);
              Doctor doctor = doctors.get(doctorId);

              if (patient != null && doctor != null) {
                  appointments.add(new Appointment(id, patient, doctor, dateTime, notes));
                  refreshTables();
              } else {
                  JOptionPane.showMessageDialog(this, "Invalid patient or doctor ID.");
              }
          } catch (DateTimeParseException e) {
              JOptionPane.showMessageDialog(this, "Invalid date/time format.");
          }
      }

      private void cancelAppointment() {
          int row = appointmentTable.getSelectedRow();
          if (row >= 0) {
              appointments.remove(row);
              refreshTables();
          } else {
              JOptionPane.showMessageDialog(this, "Select an appointment to cancel.");
          }
      }

      private void exportAppointments() {
          try {
              Sheets service = GoogleSheetsService.getSheetsService();
              GoogleSheetsService.exportAppointments(service, SPREADSHEET_ID, appointments);
              JOptionPane.show