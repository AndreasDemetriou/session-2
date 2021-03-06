/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.sbt.jschool.session2;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 */
public class OutputFormatter {
    private PrintStream out;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public OutputFormatter(PrintStream out) {
        this.out = out;
    }

    DecimalFormat df = new DecimalFormat("###,##0.00");
    DecimalFormat intdf = new DecimalFormat("###,##0");

    public void printSeparator(int z, int[] lenghts) {
        for (int i = 0; i < z; i++) {
            out.print("+");
            for (int k = 0; k < lenghts[i]; k++) {
                out.print("-");
            }
        }
        out.println("+");
    }

    public void printNames(String[] names, int[] lenghts) {
        printSeparator(names.length, lenghts);
        for (int i = 0; i < names.length; i++) {
            out.print("|");
            for (int j = 0; j < (lenghts[i] - names[i].length()) / 2; j++) {
                out.print(" ");
            }
            out.print(names[i]);
            for (int j = (lenghts[i] - names[i].length()) / 2 + names[i].length(); j < lenghts[i]; j++) {
                out.print(" ");
            }
        }
        out.println("|");
        printSeparator(names.length, lenghts);
    }

    public int[] getLengths(int size, Object[][] data,  String[] types, int[] lenghts){
        for (int j = 0; j < data[0].length; j++) {
            if (types[j].equals("class java.util.Date")) {
                lenghts[j] = lenghts[j] > 10 ? lenghts[j] : 10; //здесь проверяю отдельно, чтобы лишний раз не крутить цикл
            } else {
                for (int i = 0; i < data.length; i++) {
                    if (data[i][j] == null) continue;
                    int tmpLenght = 0;
                    switch (types[j]) {
                        case "class java.lang.String":
                            tmpLenght = data[i][j].toString().length();
                            break;
                        case "class java.lang.Integer":
                            tmpLenght = intdf.format(data[i][j]).length();
                            break;
                        case "class java.lang.Double":
                            tmpLenght = df.format(data[i][j]).length();
                            break;
                    }
                    lenghts[j] = lenghts[j] > tmpLenght ? lenghts[j] : tmpLenght;
                }
            }
        }
        return lenghts;
    }

    public void output(String[] names, Object[][] data) throws ParseException {
        int[] lenghts = new int[names.length];
        String[] types = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            lenghts[i] = names[i].length();
        }
        if (data.length == 0) {
            printNames(names, lenghts);
            return;
        }
        for (int j = 0; j < data[0].length; j++) {
            if (data[0][j] != null) types[j] = data[0][j].getClass().toString();
            else {
                int i = 1;
                while (data[i][j] == null) i++;
                types[j] = data[i][j].getClass().toString();
            }
        } //в types лежат типы столбцов
        lenghts = getLengths(names.length, data, types,lenghts);
        //теперь в lenghts лежит ширина каждого столбца
        printNames(names, lenghts);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                out.print("|");
                if (data[i][j] == null) {
                    switch (types[j]) {
                        case "class java.util.Date":
                        case "class java.lang.String":
                            out.print("-");
                            for (int k = 1; k < lenghts[j]; k++) {
                                out.print(" ");
                            }
                            break;
                        case "class java.lang.Integer":
                        case "class java.lang.Double":
                            for (int k = 1; k < lenghts[j]; k++) {
                                out.print(" ");
                            }
                            out.print("-");
                            break;
                    }
                } else {
                    switch (types[j]) {
                        case "class java.util.Date":
                            out.print(dateFormat.format(data[i][j]));
                            break;
                        case "class java.lang.String":
                            out.print(data[i][j].toString());
                            for (int k = data[i][j].toString().length(); k < lenghts[j]; k++) {
                                out.print(" ");
                            }
                            break;
                        case "class java.lang.Integer":
                            String resultString = intdf.format(data[i][j]);
                            for (int k = resultString.length(); k < lenghts[j]; k++) {
                                out.print(" ");
                            }
                            out.print(resultString);
                            break;
                        case "class java.lang.Double":
                            resultString = df.format(data[i][j]);
                            for (int k = resultString.length(); k < lenghts[j]; k++) {
                                out.print(" ");
                            }
                            out.print(resultString);
                            break;
                    }
                }
            }
            out.println("|");
            printSeparator(names.length, lenghts);
        }
    }
}
