package main.java.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.model.CapturedObjectPropModel;
import main.java.model.TestCase;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import main.java.actions.MethodType;
public class ExcelAction {
	WebDriver driver;
	static ExcelLibrary excel = new ExcelLibrary();
	static ReadConfigProperty config = new ReadConfigProperty();
	static Map<String, Object> testCaseSheet = new HashMap<String, Object>();
	static Map<String, String> readFromConfigFile = new HashMap<String, String>();
	static Map<String, Object> testSuiteSheet = new HashMap<String, Object>();
	static Map<String, Object> testDataSheet = new HashMap<String, Object>();
	static Map<String, Object> capObjPropSheet = new HashMap<String, Object>();

	static List<String> listOfTestCases = new ArrayList<String>();
	int numberOfTimeExecution = 0;
	MethodType methodtype = new MethodType();
	String testcasepth = "TestCasePath";

	public static void main(String[] args) {
		ExcelAction action = new ExcelAction();
		try {
			action.readCapturedObjectProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		action.readLocators("PAGE", "SEARCH_BOX");
	}

	/**
	 * Read test data sheet
	 */
	public void readTestDataSheet() {
		String sheetName;
		String pathOFFile = config.getConfigValues(testcasepth);
		List<String> list = ExcelLibrary.getNumberOfSheetsinTestDataSheet(config.getConfigValues(testcasepth));
		for (int i = 0; i < list.size(); i++) {
			sheetName = list.get(i);
			Map<String, Object> temp1 = new HashMap<String, Object>();
			try {
				Reporter.log("sheetName" + sheetName + "----" + "sheetName, pathOFFile" + pathOFFile);
				List listColumnNames = ExcelLibrary.getColumnNames(sheetName, pathOFFile,
						ExcelLibrary.getColumns(sheetName, pathOFFile));
				// iterate through columns in sheet
				for (int j = 0; j < listColumnNames.size(); j++) {
					// get Last Row for each Column
					int row = 1;
					List<String> listColumnValues = new ArrayList<String>();
					do {
						listColumnValues.add(ExcelLibrary.readCell(row, j, sheetName, pathOFFile));
						row++;
					} while ((ExcelLibrary.readCell(row, j, sheetName, pathOFFile)) != null);
					temp1.put((String) listColumnNames.get(j), listColumnValues);
				}
				listColumnNames.clear();
			} catch (IOException e) {
				// check after run
				MainTestNG.LOGGER.info("InvalidFormatException,IOException" + e);
			}
			testDataSheet.put(sheetName, temp1);
		}
	}

	/**
	 * Iterate over each row in testcase sheet and pass the data to execute
	 * method in MethodType.java
	 */
	public void testSuiteIterate(String tcName) {
		MainTestNG.LOGGER.info("testSuiteIterate() called  " + tcName);
		String key = tcName;

		TestCase temp = (TestCase) testCaseSheet.get(key);
		List testStepId = temp.getTestStepId();
		Reporter.log("size====" + testStepId.size());
		List dataColValues = null;
		int noOfExecution = 0;
		for (int i = 0; i < testStepId.size(); i++) {
			if (!(temp.getTestData().get(i).isEmpty())) {
				if (temp.getTestData().get(i).contains(".")) {

					String data = temp.getTestData().get(i);
					String[] testDataArray = data.split("\\.");

					dataColValues = getColumnValue(testDataArray);

					noOfExecution = dataColValues.size();

					break;
				}
			} else {
				noOfExecution = 0;
			}
		}
		MainTestNG.LOGGER.info("columnValue addedd newly numberOfTimesExecution===" + dataColValues);
		MainTestNG.LOGGER.info("testCaseExecution==" + noOfExecution);
		if (noOfExecution != 0) {
			for (int execution = 0; execution < noOfExecution; execution++) {
				for (int i = 0; i < testStepId.size(); i++) {

					String methodType = temp.getMethodType().get(i);
					String objectLocators = temp.getObjectNameFromPropertiesFile().get(i);
					String actionType = temp.getActionType().get(i);
                    String testData=temp.getTestData().get(i);
					// Data Sheet logic
					Reporter.log("get data value======" + temp.getTestData());
					if (!(temp.getTestData().get(i).isEmpty())) {
						if (temp.getTestData().get(i).contains(".")) {
							String data = temp.getTestData().get(i);
							String[] testDataArray = data.split("\\.");
							List columnValue = getColumnValue(testDataArray);
							Reporter.log("column valueee======" + columnValue);
							Reporter.log("column value size===========" + columnValue.size());
							try {
								Reporter.log("testCaseExecution======================" + noOfExecution);
								List<String> list = readLocators(methodType, objectLocators);

								methodType = list.get(0);
								objectLocators = list.get(1);
								MainTestNG.LOGGER.info("methodType=" + methodType);
								MainTestNG.LOGGER.info("objectLocators as name=" + objectLocators);
								Reporter.log("*********methodExecutor1: column valueee======" + columnValue);
								Reporter.log("********methodExecutor2: column valueee======" + columnValue.get(execution));
								methodtype.methodExecutor(methodType, objectLocators, actionType,testData);

							} catch (IndexOutOfBoundsException e) {
								String s = e.getMessage();
								throw new IndexOutOfBoundsException(
										"data column is blank..Please enter value in datasheet" + s);
							}
						}

						if (execution == noOfExecution) {
							break;
						}
					} else {
						driver = WebDriverClass.getInstance();

						List<String> list = readLocators(methodType, objectLocators);
						methodType = list.get(0);
						objectLocators = list.get(1);
						MainTestNG.LOGGER.info("methodType=" + methodType);
						methodtype.methodExecutor(methodType, objectLocators, actionType, testData);
					}
				}
				if (execution == noOfExecution) {
					break;
				}
			}

		} else {
			for (int i = 0; i < testStepId.size(); i++) {

				String methodType = temp.getMethodType().get(i);
				String objectLocators = temp.getObjectNameFromPropertiesFile().get(i);
				String actionType = temp.getActionType().get(i);
                String testData=temp.getTestData().get(i);
				driver = WebDriverClass.getInstance();

				List<String> list = readLocators(methodType, objectLocators);
				methodType = list.get(0);
				objectLocators = list.get(1);
				MainTestNG.LOGGER.info("methodType=" + methodType);
				MainTestNG.LOGGER.info("objectLocators=" + objectLocators);
				methodtype.methodExecutor(methodType, objectLocators, actionType, testData);
			}
		}
	}

	private List getColumnValue(String[] testDataArray) {

		Map<String, Object> dataSheet = (HashMap<String, Object>) testDataSheet.get(testDataArray[0]);
		List coulmnValue = (ArrayList) dataSheet.get(testDataArray[1]);
		return coulmnValue;
	}

	public void readTestSuite() {
		try {
			readFromConfigFile = config.readConfigFile();
			for (String suiteName : readFromConfigFile.values()) {
				String testSuiteFilePath = config.getConfigValues("TestSuiteName");
				System.out.println(testSuiteFilePath);
				List<String> suiteSheets = ExcelLibrary.getNumberOfSheetsinSuite(testSuiteFilePath);
				System.out.println(suiteSheets.size());
				for (int i = 0; i < suiteSheets.size(); i++) {
					String sheetName = suiteSheets.get(i);
					System.out.println(sheetName);
					if (suiteName.trim().equalsIgnoreCase(sheetName)) {
						Map<String, Object> temp1 = new HashMap<String, Object>();
						try {
							for (int row = 1; row <= ExcelLibrary.getRows(sheetName, testSuiteFilePath); row++) {
								String testCaseName = ExcelLibrary.readCell(row, 0, suiteName.trim(),
										testSuiteFilePath);
								String testCaseState = ExcelLibrary.readCell(row, 1, suiteName.trim(),
										testSuiteFilePath);
								if (("YES").equalsIgnoreCase(testCaseState)) {
									listOfTestCases.add(testCaseName);
								}
								temp1.put(testCaseName, testCaseState);
							}
							Reporter.log("listOfTestCases=============*****************" + listOfTestCases);
							testSuiteSheet.put(suiteName, temp1);
						} catch (IOException e) {

							MainTestNG.LOGGER.info("e" + e);

						}
					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void readTestCaseInExcel() throws IOException {
		String testsheetnme = "TestCase_SheetName";
		String testCasePath = config.getConfigValues(testcasepth);
		String testCaseSheetName = config.getConfigValues(testsheetnme);
		TestCase tc = null;
		try {
			for (int row = 1; row <= ExcelLibrary.getRows(testCaseSheetName, testCasePath); row++) {
				if (!(ExcelLibrary.readCell(row, 0, testCaseSheetName, testCasePath).isEmpty())) {
					tc = new TestCase();
					tc.setTestCaseName(ExcelLibrary.readCell(row, 0, testCaseSheetName, testCasePath));
					tc.setTestStepId(ExcelLibrary.readCell(row, 1, testCaseSheetName, testCasePath));
					tc.setMethodType(ExcelLibrary.readCell(row, 3, testCaseSheetName, testCasePath));
					tc.setObjectNameFromPropertiesFile(ExcelLibrary.readCell(row, 4, testCaseSheetName, testCasePath));
					tc.setActionType(ExcelLibrary.readCell(row, 5, testCaseSheetName, testCasePath));
					tc.setOnFail(ExcelLibrary.readCell(row, 6, testCaseSheetName, testCasePath));
					tc.setTestData(ExcelLibrary.readCell(row, 7, testCaseSheetName, testCasePath));
					testCaseSheet.put(ExcelLibrary.readCell(row, 0, testCaseSheetName, testCasePath), tc);
				} else {
					tc.setTestStepId(ExcelLibrary.readCell(row, 1, testCaseSheetName, testCasePath));
					tc.setMethodType(ExcelLibrary.readCell(row, 3, testCaseSheetName, testCasePath));
					tc.setObjectNameFromPropertiesFile(ExcelLibrary.readCell(row, 4, testCaseSheetName, testCasePath));
					tc.setActionType(ExcelLibrary.readCell(row, 5, testCaseSheetName, testCasePath));
					tc.setOnFail(ExcelLibrary.readCell(row, 6, testCaseSheetName, testCasePath));
					String str = ExcelLibrary.readCell(row, 7, testCaseSheetName, testCasePath);
					tc.setTestData(str);
				}
			}
		} catch (Exception e) {

			MainTestNG.LOGGER.info(e.getMessage());
		}
	}
	public void writeTestCaseInExcel() throws IOException {
		String testsheetnme = "TestCase_SheetName";
		String testCasePath = config.getConfigValues(testcasepth);
		String testCaseSheetName = config.getConfigValues(testsheetnme);
		TestCase tc = null;
		try {
			String testCaseResult=tc.getOnFail();
			//ExcelLibrary.readCell(row, 3, testCaseSheetName, testCasePath);
			} catch (Exception e) {
			MainTestNG.LOGGER.info(e.getMessage());
		}
	}

	public void clean() {
		excel.clean();

	}

	/**
	 * Capture object properties in excel sheet
	 */
	public void readCapturedObjectProperties() throws IOException {
		String testSheetName = "CapturedObjectProperties";
		String testCasePath = config.getConfigValues(testcasepth);
		MainTestNG.LOGGER.info("testCasePath==" + testCasePath);
		try {
			int totrows = ExcelLibrary.getRows(testSheetName, testCasePath);
			MainTestNG.LOGGER.info("total rows=" + totrows);
			String prevPagename = "";
			Map<String, Object> pageInfo = null;
			for (int j = 1; j <= totrows; j++) {
				String pagename = ExcelLibrary.readCell(j, 0, testSheetName, testCasePath);
				if (prevPagename.equals(pagename)) {
					String page = ExcelLibrary.readCell(j, 0, testSheetName, testCasePath);
					String name = ExcelLibrary.readCell(j, 1, testSheetName, testCasePath);
					String property = ExcelLibrary.readCell(j, 2, testSheetName, testCasePath);
					String value = ExcelLibrary.readCell(j, 3, testSheetName, testCasePath);
					CapturedObjectPropModel capModel = new CapturedObjectPropModel();
					capModel.setPage(page);
					capModel.setName(name);
					capModel.setProperty(property);
					capModel.setValue(value);
					MainTestNG.LOGGER.info(capModel.getPage() + "  " + capModel.getName() + "  " + capModel.getValue()
							+ "  " + capModel.getProperty());
					pageInfo.put(name, capModel);
				} else {
					if (prevPagename != null) {
						capObjPropSheet.put(prevPagename, pageInfo);
					}
					pageInfo = new HashMap<String, Object>();
					String page = ExcelLibrary.readCell(j, 0, testSheetName, testCasePath);
					String name = ExcelLibrary.readCell(j, 1, testSheetName, testCasePath);
					String property = ExcelLibrary.readCell(j, 2, testSheetName, testCasePath);
					String value = ExcelLibrary.readCell(j, 3, testSheetName, testCasePath);
					CapturedObjectPropModel capModel = new CapturedObjectPropModel();
					capModel.setPage(pagename);
					capModel.setName(name);
					capModel.setProperty(property);
					capModel.setValue(value);
					pageInfo.put(name, capModel);
					prevPagename = pagename;
				}
				if (prevPagename != null) {
					capObjPropSheet.put(prevPagename, pageInfo);
				}
			}
		} catch (Exception e) {

			MainTestNG.LOGGER.info("InvalidFormatException=" + e);
		}
	}

	public List<String> readLocators(String page, String name) {
		MainTestNG.LOGGER.info(page);
		MainTestNG.LOGGER.info(name);
		Map<String, Object> temp = (Map<String, Object>) capObjPropSheet.get(page);
		List<String> locators = new ArrayList<>();
		MainTestNG.LOGGER.info("objects" + capObjPropSheet.get(page));
		if (capObjPropSheet.get(page) != null) {

			MainTestNG.LOGGER.info("name" + temp.get(name));
			CapturedObjectPropModel c = (CapturedObjectPropModel) temp.get(name);
			MainTestNG.LOGGER.info(c.getName());
			MainTestNG.LOGGER.info("c.getPage()=" + c.getPage());

			if (c.getPage().equals(page) && c.getName().equals(name)) {
				locators.add(c.getProperty());
				locators.add(c.getValue());
				MainTestNG.LOGGER.info("locators" + locators);
			}
		}
		MainTestNG.LOGGER.info("size" + locators.size());
		return locators;
	}
}
