/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser.fixes;

import dsdtparser.ActionForm;
import dsdtparser.DSDTParserView;
import dsdtparser.parser.ActionParser;
import dsdtparser.parser.DSDTItem;
import dsdtparser.parser.InvalidParameterException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoFix {
    public static String[] knownErrors = new String[]{"string must be entirely alphanumeric", "invalid combination of length and min/max fixed flags", "method local variable is not initialized", "length is larger than min/max window", "invalid object type for reserved name (found integer, requires buffer)", "invalid object type for reserved name (found package, requires integer)", "invalid object type for reserved name (found buffer, requires package)", "invalid object type for reserved name (found zero, requires buffer)", "invalid object type for reserved name (found one, requires buffer)", "invalid object type for reserved name (found package, requires buffer)", "length is not equal to fixed min/max window", "non-hex letters must be upper case", "min/max/length/gran are all zero, but no resource tag", "invalid object type for reserved name (found var_package, requires package)", "_hid string must be exactly 7 or 8 characters", "syntax error, unexpected parseop_if", "invalid object type for reserved name (found buffer, requires integer)", "syntax error, unexpected parseop_store", "reserved name must be a control method (with zero arguments)", "address min is greater than address max", "syntax error, unexpected parseop_else", "syntax error, unexpected parseop_and"};
    private static Pattern argument = Pattern.compile("\\(([^\\)]+)\\)");
    private static Pattern hexval = Pattern.compile("0x([a-fA-F0-9]{16})");
    private static Pattern hex8val = Pattern.compile("0x([a-fA-F0-9]{8})");
    private static Pattern hex4val = Pattern.compile("0x([a-fA-F0-9]{4})");
    private static Pattern hex2val = Pattern.compile("0x([a-fA-F0-9]{2})");
    private static String buffer;
    private static String[] lineBuffer;

    public static boolean isKnownError(String line) {
        line = line.toLowerCase();
        for (int i = 0; i < knownErrors.length; ++i) {
            if (line.indexOf(knownErrors[i]) == -1) continue;
            return true;
        }
        return false;
    }

    public static void setBuffer(String buffern) {
        buffer = buffern;
    }

    public static String getBuffer() {
        return buffer;
    }

    public static void loadLineBuffer() {
        lineBuffer = buffer.split("\n");
    }

    public static String getLineAt(int number) {
        --number;
        if (lineBuffer == null) {
            AutoFix.loadLineBuffer();
        }
        return lineBuffer[number];
    }

    public static void fix(DSDTParserView view, String error, int line) {
        String nerror = error.toLowerCase();
        if (nerror.indexOf(knownErrors[1]) != -1 || nerror.indexOf(knownErrors[3]) != -1 || nerror.indexOf(knownErrors[10]) != -1) {
            String tmp = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String maxstr = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String minstr = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            String maxAlgo = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 5);
            String oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line);
            Matcher m = hexval.matcher(minstr);
            long min = 0;
            long max = 0;
            if (m.find()) {
                min = AutoFix.toLongValue(m.group(1));
                m = hexval.matcher(maxstr);
                if (m.find()) {
                    max = AutoFix.toLongValue(m.group(1));
                }
                long length = max - min + 1;
                if (maxAlgo.contains("MinNotFixed, MaxFixed") || maxAlgo.contains("MinFixed, MaxNotFixed")) {
                    length = 0;
                }
                m = hexval.matcher(oldline);
                m.find();
                String newline = m.replaceFirst("0x" + AutoFix.toHexString(length));
                String oldVal = minstr + "\n" + maxstr + "\n" + tmp + "\n" + oldline;
                String newVal = minstr + "\n" + maxstr + "\n" + tmp + "\n" + newline;
                if (view == null) {
                    buffer = buffer.replace(oldVal, newVal);
                } else {
                    view.performReplace(oldVal, newVal);
                }
            } else {
                m = hex8val.matcher(minstr);
                if (m.find()) {
                    min = AutoFix.toLongValue(m.group(1));
                    m = hex8val.matcher(maxstr);
                    if (m.find()) {
                        max = AutoFix.toLongValue(m.group(1));
                    }
                    long length = max - min + 1;
                    if (maxAlgo.contains("MinNotFixed, MaxFixed") || maxAlgo.contains("MinFixed, MaxNotFixed")) {
                        length = 0;
                    }
                    m = hex8val.matcher(oldline);
                    m.find();
                    String newline = m.replaceFirst("0x" + AutoFix.to8HexString(length));
                    String oldVal = minstr + "\n" + maxstr + "\n" + tmp + "\n" + oldline;
                    String newVal = minstr + "\n" + maxstr + "\n" + tmp + "\n" + newline;
                    if (view == null) {
                        buffer = buffer.replace(oldVal, newVal);
                    } else {
                        view.performReplace(oldVal, newVal);
                    }
                } else {
                    m = hex4val.matcher(minstr);
                    if (m.find()) {
                        min = AutoFix.toLongValue(m.group(1));
                        m = hex4val.matcher(maxstr);
                        if (m.find()) {
                            max = AutoFix.toLongValue(m.group(1));
                        }
                        long length = max - min + 1;
                        if (maxAlgo.contains("MinNotFixed, MaxFixed") || maxAlgo.contains("MinFixed, MaxNotFixed")) {
                            length = 0;
                        }
                        m = hex4val.matcher(oldline);
                        m.find();
                        String newline = m.replaceFirst("0x" + AutoFix.to4HexString(length));
                        String oldVal = minstr + "\n" + maxstr + "\n" + tmp + "\n" + oldline;
                        String newVal = minstr + "\n" + maxstr + "\n" + tmp + "\n" + newline;
                        if (view == null) {
                            buffer = buffer.replace(oldVal, newVal);
                        } else {
                            view.performReplace(oldVal, newVal);
                        }
                    }
                }
            }
        } else if (nerror.indexOf(knownErrors[12]) != -1) {
            String seguro = view == null ? AutoFix.getLineAt(line + 5) : view.getLineAt(line + 5);
            String lengthstr = view == null ? AutoFix.getLineAt(line + 4) : view.getLineAt(line + 4);
            String algo = view == null ? AutoFix.getLineAt(line + 3) : view.getLineAt(line + 3);
            String maxstr = view == null ? AutoFix.getLineAt(line + 1) : view.getLineAt(line + 1);
            String minstr = view == null ? AutoFix.getLineAt(line + 2) : view.getLineAt(line + 2);
            String oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line);
            long min = 0;
            long max = 0;
            if (maxstr.contains("Granularity")) {
                Matcher m = hexval.matcher(minstr);
                if (m.find()) {
                    min = AutoFix.toLongValue(m.group(1));
                    m = hexval.matcher(algo);
                    if (m.find()) {
                        max = AutoFix.toLongValue(m.group(1));
                    }
                    long length = 1;
                    m = hexval.matcher(seguro);
                    m.find();
                    String newline = m.replaceFirst("0x" + AutoFix.toHexString(length));
                    String oldVal = oldline + "\n" + maxstr + "\n" + minstr + "\n" + algo + "\n" + lengthstr + "\n" + seguro;
                    String newVal = oldline + "\n" + maxstr + "\n" + minstr + "\n" + algo + "\n" + lengthstr + "\n" + newline;
                    if (view == null) {
                        buffer = buffer.replace(oldVal, newVal);
                    } else {
                        view.performReplace(oldVal, newVal);
                    }
                }
            } else {
                Matcher m = hexval.matcher(minstr);
                if (m.find()) {
                    min = AutoFix.toLongValue(m.group(1));
                    m = hexval.matcher(algo);
                    if (m.find()) {
                        max = AutoFix.toLongValue(m.group(1));
                    }
                    long length = max - min + 1;
                    m = hexval.matcher(seguro);
                    m.find();
                    String newline = m.replaceFirst("0x" + AutoFix.toHexString(length));
                    String oldVal = oldline + "\n" + maxstr + "\n" + minstr + "\n" + algo + "\n" + lengthstr + "\n" + seguro;
                    String newVal = oldline + "\n" + maxstr + "\n" + minstr + "\n" + algo + "\n" + lengthstr + "\n" + newline;
                    if (view == null) {
                        buffer = buffer.replace(oldVal, newVal);
                    } else {
                        view.performReplace(oldVal, newVal);
                    }
                } else {
                    m = hex4val.matcher(minstr);
                    if (m.find()) {
                        min = AutoFix.toLongValue(m.group(1));
                        m = hex4val.matcher(maxstr);
                        if (m.find()) {
                            max = AutoFix.toLongValue(m.group(1));
                        }
                        long length = max - min + 1;
                        m = hex2val.matcher(lengthstr);
                        if (m.find()) {
                            String newline = m.replaceFirst("0x" + AutoFix.to2HexString(length));
                            String oldVal = oldline + "\n" + maxstr + "\n" + minstr + "\n" + algo + "\n" + lengthstr + "\n" + seguro;
                            String newVal = oldline + "\n" + maxstr + "\n" + minstr + "\n" + algo + "\n" + newline + "\n" + seguro;
                            if (view == null) {
                                buffer = buffer.replace(oldVal, newVal);
                            } else {
                                view.performReplace(oldVal, newVal);
                            }
                        }
                    }
                }
            }
        } else if (nerror.indexOf(knownErrors[0]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("(Name\\s*\\(_HID,\\s*\")\\*([a-zA-Z]{3,4})([0-9a-fA-F]{4})\"\\)");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append(m.group(1)).append(m.group(2).toUpperCase()).append(m.group(3)).append("\")").toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[14]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("(Name\\s*\\(_HID,\\s*\")([0-9a-zA-Z]+)\"\\)");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                String novoNome = m.group(2);
                if (novoNome.length() > 8) {
                    novoNome = novoNome.substring(0, 8);
                } else {
                    while (novoNome.length() < 7) {
                        novoNome = novoNome + "0";
                    }
                }
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append(m.group(1)).append(novoNome).append("\")").toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[2]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("(Store\\s*\\()Local0(,\\s*Local0\\s*\\))");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append(m.group(1)).append("\"Local0\"").append(m.group(2)).toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            } else {
                p = Pattern.compile("(^.*)Local(\\d)(.*$)");
                m = p.matcher(oldline);
                if (m.find()) {
                    oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                    String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append(m.group(1)).append("\"Local").append(m.group(2)).append("\"").append(m.group(3)).toString());
                    if (view == null) {
                        buffer = buffer.replace(oldline, newline);
                    } else {
                        view.performReplace(oldline, newline);
                    }
                }
            }
        } else if (nerror.indexOf(knownErrors[4]) != -1 || nerror.indexOf(knownErrors[7]) != -1 || nerror.indexOf(knownErrors[8]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            String line4 = view == null ? AutoFix.getLineAt(line - 4) : view.getLineAt(line - 4);
            String line5 = view == null ? AutoFix.getLineAt(line - 5) : view.getLineAt(line - 5);
            String linep1 = view == null ? AutoFix.getLineAt(line + 1) : view.getLineAt(line + 1);
            String linep2 = view == null ? AutoFix.getLineAt(line + 2) : view.getLineAt(line + 2);
            String linep3 = view == null ? AutoFix.getLineAt(line + 3) : view.getLineAt(line + 3);
            Pattern p = Pattern.compile("Return \\((Zero|One|0x[0-9a-fA-F]+)\\)");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line5 + "\n" + line4 + "\n" + line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline + "\n" + linep1 + "\n" + linep2 + "\n" + linep3;
                String newline = line5 + "\n" + line4 + "\n" + line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append("Return (Buffer (One) {").append(m.group(1)).append("})").toString()) + "\n" + linep1 + "\n" + linep2 + "\n" + linep3;
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[5]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("Return ?\\(Package ?\\(0x02\\) ?\\{0x00, ?0x00\\}\\)");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll("Return (Zero)");
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[6]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            String linep1 = view == null ? AutoFix.getLineAt(line + 1) : view.getLineAt(line + 1);
            String linep2 = view == null ? AutoFix.getLineAt(line + 2) : view.getLineAt(line + 2);
            String linep3 = view == null ? AutoFix.getLineAt(line + 3) : view.getLineAt(line + 3);
            Pattern p = Pattern.compile("(Name ?\\([^,]+, ?)Buffer ?\\([^)]+\\)");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                int count = 0;
                String ln = "";
                Pattern hex = Pattern.compile("0x[0-9a-fA-F]+");
                for (int i = 1; i < 10 && ln.indexOf("}") == -1; ++i) {
                    ln = view == null ? AutoFix.getLineAt(line + i) : view.getLineAt(line + i);
                    Matcher n = hex.matcher(ln);
                    while (n.find()) {
                        ++count;
                    }
                }
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline + "\n" + linep1 + "\n" + linep2 + "\n" + linep3;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append(m.group(1)).append("Package (0x").append(AutoFix.toSmallHexString(count)).append(")").toString()) + "\n" + linep1 + "\n" + linep2 + "\n" + linep3;
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[9]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("(Name\\s*\\(.*,\\s*)Package(.*)$");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append(m.group(1)).append("Buffer").append(m.group(2)).toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[11]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("\"\\*?([a-zA-Z]{3,4})([0-9a-fA-F]{4})\"");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append("\"").append(m.group(1).toUpperCase()).append(m.group(2)).append("\"").toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            } else {
                p = Pattern.compile("Name \\(_(...),");
                m = p.matcher(oldline);
                if (m.find()) {
                    oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                    String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append("Name (").append(m.group(1)).append(",").toString());
                    if (view == null) {
                        buffer = buffer.replace(oldline, newline);
                    } else {
                        view.performReplace(oldline, newline);
                    }
                }
            }
        } else if (nerror.indexOf(knownErrors[13]) != -1) {
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("(Name\\s*\\(_PCL,\\s*Package\\s*\\()One\\)");
            Pattern p2 = Pattern.compile("(Name\\s*\\(_PCL,\\s*Package\\s*\\()Zero\\)");
            String oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line);
            Matcher m = p.matcher(oldline);
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append(m.group(1)).append("0x01)").toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            } else {
                m = p2.matcher(oldline);
                if (m.find()) {
                    oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                    String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append(m.group(1)).append("0x00)").toString());
                    if (view == null) {
                        buffer = buffer.replace(oldline, newline);
                    } else {
                        view.performReplace(oldline, newline);
                    }
                }
            }
        } else if (nerror.indexOf(knownErrors[15]) != -1 || nerror.indexOf(knownErrors[20]) != -1) {
            String buf;
            String repBuffer = buf = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line);
            buf = "//" + buf;
            Pattern open = Pattern.compile("^\\s*\\{\\s*$");
            Pattern close = Pattern.compile("^\\s*\\}");
            String line1 = view == null ? AutoFix.getLineAt(line + 1) : view.getLineAt(line + 1);
            Matcher m = open.matcher(line1);
            if (m.find()) {
                buf = buf + "\n//" + line1;
                repBuffer = repBuffer + "\n" + line1;
                int count = 1;
                int i = 2;
                while (count > 0) {
                    line1 = view == null ? AutoFix.getLineAt(line + i) : view.getLineAt(line + i);
                    m = open.matcher(line1);
                    if (m.find()) {
                        ++count;
                    }
                    if ((m = close.matcher(line1)).find()) {
                        --count;
                    }
                    buf = count == 0 ? buf + "\n//" + line1 : buf + "\n" + line1;
                    repBuffer = repBuffer + "\n" + line1;
                    ++i;
                }
                if (view == null) {
                    buffer = buffer.replace(repBuffer, buf);
                } else {
                    view.performReplace(repBuffer, buf);
                }
            }
        } else if (nerror.indexOf(knownErrors[21]) != -1) {
            String buf = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line);
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            String line11 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line + 1);
            String line22 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line + 2);
            String line33 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line + 3);
            String nbuf = "//" + buf;
            String oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + buf + "\n" + line11 + "\n" + line22 + "\n" + line33;
            String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + nbuf + "\n" + line11 + "\n" + line22 + "\n" + line33;
            if (view == null) {
                buffer = buffer.replace(oldline, newline);
            } else {
                view.performReplace(oldline, newline);
            }
        } else if (nerror.indexOf(knownErrors[16]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("Return \\(Buffer \\(One\\) \\{(Zero|One|0x[0-9a-fA-F]+)\\}\\)");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append("Return (").append(m.group(1)).append(")").toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[17]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("Store \\((RTMP) \\((TPMP)\\), Store \\((RTNP), (TPNP)\\)\\)");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append("Store (").append(m.group(1)).append(", ").append(m.group(2)).append(") Store(").append(m.group(3)).append(", ").append(m.group(4)).append(")").toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[18]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            Pattern p = Pattern.compile("Name \\(_(.*$)");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append("Name (").append(m.group(1)).toString());
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        } else if (nerror.indexOf(knownErrors[19]) != -1) {
            String oldline;
            String line1 = view == null ? AutoFix.getLineAt(line - 1) : view.getLineAt(line - 1);
            String line2 = view == null ? AutoFix.getLineAt(line - 2) : view.getLineAt(line - 2);
            String line3 = view == null ? AutoFix.getLineAt(line - 3) : view.getLineAt(line - 3);
            String linep1 = view == null ? AutoFix.getLineAt(line + 1) : view.getLineAt(line + 1);
            String linep2 = view == null ? AutoFix.getLineAt(line + 2) : view.getLineAt(line + 2);
            String linep3 = view == null ? AutoFix.getLineAt(line + 3) : view.getLineAt(line + 3);
            Pattern p = Pattern.compile("0x([0-9A-Fa-f]+),");
            Matcher m = p.matcher(oldline = view == null ? AutoFix.getLineAt(line) : view.getLineAt(line));
            if (m.find()) {
                String ln = "0";
                while (ln.length() < m.group(1).length()) {
                    ln = "0" + ln;
                }
                oldline = line3 + "\n" + line2 + "\n" + line1 + "\n" + oldline + "\n" + linep1 + "\n" + linep2 + "\n" + linep3;
                String newline = line3 + "\n" + line2 + "\n" + line1 + "\n" + m.replaceAll(new StringBuilder().append("0x").append(ln).append(",").toString()) + "\n" + linep1 + "\n" + linep2 + "\n" + linep3;
                if (view == null) {
                    buffer = buffer.replace(oldline, newline);
                } else {
                    view.performReplace(oldline, newline);
                }
            }
        }
    }

    private static String toSmallHexString(long val) {
        String ret = Long.toHexString(val);
        while (ret.length() < 2) {
            ret = "0" + ret;
        }
        if (ret.length() > 2) {
            return AutoFix.toSmallHexString(val - 1);
        }
        return ret;
    }

    private static String toHexString(long val) {
        String ret = Long.toHexString(val);
        while (ret.length() < 16) {
            ret = "0" + ret;
        }
        if (ret.length() > 16) {
            return AutoFix.toHexString(val - 1);
        }
        return ret;
    }

    private static String to8HexString(long val) {
        String ret = Long.toHexString(val);
        while (ret.length() < 8) {
            ret = "0" + ret;
        }
        if (ret.length() > 8) {
            return AutoFix.to8HexString(val - 1);
        }
        return ret;
    }

    private static String to4HexString(long val) {
        String ret = Long.toHexString(val);
        while (ret.length() < 4) {
            ret = "0" + ret;
        }
        if (ret.length() > 4) {
            return AutoFix.to4HexString(val - 1);
        }
        return ret;
    }

    private static String to2HexString(long val) {
        String ret = Long.toHexString(val);
        while (ret.length() < 2) {
            ret = "0" + ret;
        }
        if (ret.length() > 2) {
            return AutoFix.to2HexString(val - 1);
        }
        return ret;
    }

    private static long toLongValue(String val) {
        return Long.parseLong(val, 16);
    }

    private static void fixNotAllControls(DSDTParserView view, String method) {
        String patch = "into method label " + method + " insert begin Return (Package (0x02) {0x00, 0x00}) end";
        ArrayList<DSDTItem> parser = ActionForm.generateItems(view.getParser().getRoot());
        ActionParser ap = new ActionParser(parser);
        try {
            ap.parse(patch, true);
        }
        catch (InvalidParameterException ex) {
            Logger.getLogger(AutoFix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static {
        lineBuffer = null;
    }
}

