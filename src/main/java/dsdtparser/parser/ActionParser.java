/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser.parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ActionParser {
    private static final boolean DEBUG = true;
    private Pattern[] commands;
    private ArrayList<DSDTItem> parser;
    private String var8;
    private String var9;
    private Pattern adr = Pattern.compile("Name \\(_ADR, ([^\\)]+)\\)");
    private Pattern hid = Pattern.compile("Name \\(_HID, EisaId \\(\"([^\"]+)\"\\)\\)");
    private Pattern variavel = Pattern.compile("\\%(\\d)");

    public ActionParser(ArrayList<DSDTItem> parser) {
        this.parser = parser;
        this.commands = new Pattern[4];
        this.commands[0] = Pattern.compile("^\\s*into ([^ ]+)((?: [^ ]+\\s[^ ]+)+) ([^\\s]+) begin (.+) end\\s*?$");
        this.commands[1] = Pattern.compile("^\\s*into ([^ ]+)((?: [^ ]+\\s[^ ]+)+) ([^ ]+)\\s*?$");
        this.commands[2] = Pattern.compile("^\\s*into_all ([^ ]+)((?: [^ ]+\\s[^ ]+)+) ([^\\s]+) begin (.+) end\\s*?$");
        this.commands[3] = Pattern.compile("^\\s*into_all ([^ ]+)((?: [^ ]+\\s[^ ]+)+) ([^ ]+)\\s*?$");
    }

    public String[] parse(String text, boolean apply) throws InvalidParameterException {
        Matcher m = this.commands[0].matcher(text);
        if (m.find()) {
            System.out.println("matched 0");
            String type = m.group(1);
            String[] tmp = m.group(2).substring(1).split("\\s");
            String[] props = new String[tmp.length / 2];
            String[] values = new String[tmp.length / 2];
            for (int k = 0; k < tmp.length; ++k) {
                if (k % 2 == 0) {
                    props[k / 2] = tmp[k];
                    continue;
                }
                values[k / 2] = this.actionValueNormalize(tmp[k]);
            }
            String action = m.group(3);
            String avalue = this.actionValueNormalize(m.group(4));
            int tp = this.getType(type);
            if (tp == -1) {
                throw new InvalidParameterException("Invalid type: " + type);
            }
            int[] pp = new int[props.length];
            for (int k2 = 0; k2 < props.length; ++k2) {
                pp[k2] = this.getProp(props[k2]);
                if (pp[k2] != -1) continue;
                throw new InvalidParameterException("Invalid property: " + props[k2]);
            }
            int ac = this.getAction(action);
            if (ac == -1) {
                throw new InvalidParameterException("Invalid action: " + action);
            }
            if (this.parser != null) {
                for (int k3 = 0; k3 < this.parser.size(); ++k3) {
                    DSDTItem item = this.parser.get(k3);
                    if (!this.typeMatches(item, tp)) continue;
                    int counter = 0;
                    for (int l = 0; l < pp.length && this.propMatches(item, pp[l], values[l]); ++l) {
                        ++counter;
                    }
                    if (counter != pp.length) continue;
                    if (ac < 40) {
                        if (apply) {
                            String a = item.printEntry();
                            this.performAction(item, values[values.length - 1], ac, avalue);
                            String b = item.printEntry();
                            return new String[]{a, b};
                        }
                        String a = item.printEntry();
                        DSDTItem itemclone = item.clone();
                        this.performAction(itemclone, values[values.length - 1], ac, avalue);
                        String b = itemclone.printEntry();
                        return new String[]{a, b};
                    }
                    throw new InvalidParameterException("Invalid action for command pattern");
                }
            }
        } else {
            m = this.commands[1].matcher(text);
            if (m.find()) {
                String type = m.group(1);
                String[] tmp = m.group(2).substring(1).split(" ");
                String[] props = new String[tmp.length / 2];
                String[] values = new String[tmp.length / 2];
                for (int k = 0; k < tmp.length; ++k) {
                    if (k % 2 == 0) {
                        props[k / 2] = tmp[k];
                        continue;
                    }
                    values[k / 2] = this.actionValueNormalize(tmp[k]);
                }
                String action = m.group(3);
                int tp = this.getType(type);
                if (tp == -1) {
                    throw new InvalidParameterException("Invalid type: " + type);
                }
                int[] pp = new int[props.length];
                for (int k4 = 0; k4 < props.length; ++k4) {
                    pp[k4] = this.getProp(props[k4]);
                    if (pp[k4] != -1) continue;
                    throw new InvalidParameterException("Invalid property: " + props[k4]);
                }
                int ac = this.getAction(action);
                if (ac == -1) {
                    throw new InvalidParameterException("Invalid action: " + action);
                }
                System.out.println("matched 1 action " + action + " = " + ac);
                if (this.parser != null) {
                    for (int k5 = 0; k5 < this.parser.size(); ++k5) {
                        DSDTItem item = this.parser.get(k5);
                        if (!this.typeMatches(item, tp)) continue;
                        int counter = 0;
                        for (int l = 0; l < pp.length && this.propMatches(item, pp[l], values[l]); ++l) {
                            ++counter;
                        }
                        if (counter != pp.length) continue;
                        if (ac >= 40) {
                            if (apply) {
                                String a = item.printEntry();
                                this.performAction(item, values[values.length - 1], ac);
                                String b = item.printEntry();
                                return new String[]{a, b};
                            }
                            String a = item.printEntry();
                            DSDTItem itemclone = item.clone();
                            this.performAction(itemclone, values[values.length - 1], ac);
                            String b = itemclone.printEntry();
                            System.out.println(a + b);
                            return new String[]{a, b};
                        }
                        throw new InvalidParameterException("Invalid action for command pattern");
                    }
                }
            } else {
                m = this.commands[2].matcher(text);
                if (m.find()) {
                    System.out.println("matched 2");
                    String buffer1 = "";
                    String buffer2 = "";
                    String type = m.group(1);
                    String[] tmp = m.group(2).substring(1).split("\\s");
                    String[] props = new String[tmp.length / 2];
                    String[] values = new String[tmp.length / 2];
                    for (int k = 0; k < tmp.length; ++k) {
                        if (k % 2 == 0) {
                            props[k / 2] = tmp[k];
                            continue;
                        }
                        values[k / 2] = this.actionValueNormalize(tmp[k]);
                    }
                    String action = m.group(3);
                    String avalue = this.actionValueNormalize(m.group(4));
                    int tp = this.getType(type);
                    if (tp == -1) {
                        throw new InvalidParameterException("Invalid type: " + type);
                    }
                    int[] pp = new int[props.length];
                    for (int k6 = 0; k6 < props.length; ++k6) {
                        pp[k6] = this.getProp(props[k6]);
                        if (pp[k6] != -1) continue;
                        throw new InvalidParameterException("Invalid property: " + props[k6]);
                    }
                    int ac = this.getAction(action);
                    if (ac == -1) {
                        throw new InvalidParameterException("Invalid action: " + action);
                    }
                    if (this.parser != null) {
                        for (int k7 = 0; k7 < this.parser.size(); ++k7) {
                            DSDTItem item = this.parser.get(k7);
                            if (!this.typeMatches(item, tp)) continue;
                            int counter = 0;
                            for (int l = 0; l < pp.length && this.propMatches(item, pp[l], values[l]); ++l) {
                                ++counter;
                            }
                            if (counter != pp.length) continue;
                            if (ac < 40) {
                                if (apply) {
                                    String a = item.printEntry();
                                    buffer1 = buffer1 + a;
                                    this.performAction(item, values[values.length - 1], ac, avalue);
                                    String b = item.printEntry();
                                    buffer2 = buffer2 + a;
                                    continue;
                                }
                                String a = item.printEntry();
                                buffer1 = buffer1 + a;
                                DSDTItem itemclone = item.clone();
                                this.performAction(itemclone, values[values.length - 1], ac, avalue);
                                String b = itemclone.printEntry();
                                buffer2 = buffer2 + b;
                                continue;
                            }
                            throw new InvalidParameterException("Invalid action for command pattern");
                        }
                        return new String[]{buffer1, buffer2};
                    }
                } else {
                    m = this.commands[3].matcher(text);
                    if (m.find()) {
                        System.out.println("matched 3");
                        String buffer1 = "";
                        String buffer2 = "";
                        String type = m.group(1);
                        String[] tmp = m.group(2).substring(1).split(" ");
                        String[] props = new String[tmp.length / 2];
                        String[] values = new String[tmp.length / 2];
                        for (int k = 0; k < tmp.length; ++k) {
                            if (k % 2 == 0) {
                                props[k / 2] = tmp[k];
                                continue;
                            }
                            values[k / 2] = this.actionValueNormalize(tmp[k]);
                        }
                        String action = m.group(3);
                        int tp = this.getType(type);
                        if (tp == -1) {
                            throw new InvalidParameterException("Invalid type: " + type);
                        }
                        int[] pp = new int[props.length];
                        for (int k8 = 0; k8 < props.length; ++k8) {
                            pp[k8] = this.getProp(props[k8]);
                            if (pp[k8] != -1) continue;
                            throw new InvalidParameterException("Invalid property: " + props[k8]);
                        }
                        int ac = this.getAction(action);
                        if (ac == -1) {
                            throw new InvalidParameterException("Invalid action: " + action);
                        }
                        if (this.parser != null) {
                            for (int k9 = 0; k9 < this.parser.size(); ++k9) {
                                DSDTItem item = this.parser.get(k9);
                                if (!this.typeMatches(item, tp)) continue;
                                int counter = 0;
                                for (int l = 0; l < pp.length && this.propMatches(item, pp[l], values[l]); ++l) {
                                    ++counter;
                                }
                                if (counter != pp.length) continue;
                                if (ac >= 40) {
                                    if (apply) {
                                        String a = item.printEntry();
                                        buffer1 = buffer1 + a;
                                        this.performAction(item, values[values.length - 1], ac);
                                        String b = item.printEntry();
                                        buffer2 = buffer2 + b;
                                        continue;
                                    }
                                    String a = item.printEntry();
                                    buffer1 = buffer1 + a;
                                    DSDTItem itemclone = item.clone();
                                    this.performAction(itemclone, values[values.length - 1], ac);
                                    String b = itemclone.printEntry();
                                    buffer2 = buffer2 + b;
                                    continue;
                                }
                                throw new InvalidParameterException("Invalid action for command pattern");
                            }
                        }
                        return new String[]{buffer1, buffer2};
                    }
                    throw new InvalidParameterException("Invalid command");
                }
            }
        }
        return new String[]{"", ""};
    }

    private void performAction(DSDTItem item, String prop, int action) throws InvalidParameterException {
        if (action == 40 || action == 41) {
            Pattern p = Pattern.compile(prop);
            Matcher m = p.matcher(item.getCodigo());
            if (action == 40) {
                item.setCodigo(m.replaceFirst(""));
            } else if (action == 41) {
                item.setCodigo(m.replaceAll(""));
            }
        } else if (action == 42) {
            item.delete();
        } else {
            if (action == 43) {
                try {
                    Pattern p = Pattern.compile(prop);
                    Matcher m = p.matcher(item.getCodigo());
                    m.find();
                    this.var8 = m.group(1);
                }
                catch (IllegalStateException e) {
                    throw new InvalidParameterException("You need to define a group to store");
                }
            }
            if (action == 44) {
                try {
                    Pattern p = Pattern.compile(prop);
                    Matcher m = p.matcher(item.getCodigo());
                    m.find();
                    this.var9 = m.group(1);
                }
                catch (IllegalStateException e) {
                    throw new InvalidParameterException("You need to define a group to store");
                }
            }
        }
    }

    private void performAction(DSDTItem item, String prop, int action, String value) throws InvalidParameterException {
        if (action == 2 || action == 3) {
            Pattern p = Pattern.compile(prop);
            Matcher m = p.matcher(item.getCodigo());
            m.find();
            if (action == 2) {
                item.setCodigo(m.replaceFirst(this.actionValueReplaceVars(value, m)));
            } else if (action == 3) {
                item.setCodigo(m.replaceAll(this.actionValueReplaceVars(value, m)));
            }
        } else if (action == 0) {
            item.insert(this.actionValueReplaceVars(value, null));
        } else if (action == 1) {
            item.getParametro()[0] = value;
        } else if (action == 4) {
            item.setCodigo(value);
        }
    }

    private boolean propMatches(DSDTItem item, int prop, String value) {
        if (prop == 0) {
            try {
                if (value.equalsIgnoreCase(item.getParametro()[0])) {
                    return true;
                }
                return false;
            }
            catch (ArrayIndexOutOfBoundsException ex) {
            }
            catch (NullPointerException ex) {
                // empty catch block
            }
            return false;
        }
        if (prop == 1) {
            try {
                Pattern p = Pattern.compile(value);
                Matcher m = p.matcher(item.getCodigo());
                if (m.find()) {
                    return true;
                }
                return false;
            }
            catch (Exception e) {
                return false;
            }
        }
        if (prop == 2) {
            Matcher m = this.adr.matcher(item.getCodigo());
            if (m.find() && m.group(1).equalsIgnoreCase(value)) {
                return true;
            }
            return false;
        }
        if (prop == 3) {
            Matcher m = this.hid.matcher(item.getCodigo());
            if (m.find() && m.group(1).equalsIgnoreCase(value)) {
                return true;
            }
            return false;
        }
        if (prop == 4) {
            try {
                if (value.equalsIgnoreCase(item.getParent().getParametro()[0])) {
                    return true;
                }
                return false;
            }
            catch (ArrayIndexOutOfBoundsException ex) {
            }
            catch (NullPointerException ex) {
                // empty catch block
            }
            return false;
        }
        if (prop == 5) {
            try {
                if (value.equalsIgnoreCase(item.getParent().getTipo())) {
                    return true;
                }
                return false;
            }
            catch (NullPointerException ex) {
                return false;
            }
        }
        if (prop == 6) {
            try {
                Pattern p = Pattern.compile(value);
                Matcher m = p.matcher(item.getCodigo());
                if (!m.find()) {
                    return true;
                }
                return false;
            }
            catch (Exception e) {
                return false;
            }
        }
        if (prop == 7) {
            try {
                Matcher m = this.adr.matcher(item.getParent().getCodigo());
                if (m.find() && m.group(1).equalsIgnoreCase(value)) {
                    return true;
                }
            }
            catch (NullPointerException e) {
                // empty catch block
            }
            return false;
        }
        if (prop == 8) {
            try {
                Matcher m = this.hid.matcher(item.getParent().getCodigo());
                if (m.find() && m.group(1).equalsIgnoreCase(value)) {
                    return true;
                }
            }
            catch (NullPointerException e) {
                // empty catch block
            }
            return false;
        }
        return false;
    }

    private boolean typeMatches(DSDTItem item, int tipo) {
        if (item.getTipoInt() == tipo || tipo == 100) {
            return true;
        }
        return false;
    }

    private String actionValueNormalize(String arg) {
        arg = arg.replaceAll("\\\\n", "\n");
        return arg;
    }

    private String actionValueReplaceVars(String arg, Matcher m) throws InvalidParameterException {
        Matcher t = this.variavel.matcher(arg);
        while (t.find()) {
            try {
                String key = "%" + t.group(1);
                int num = Integer.parseInt(t.group(1));
                if (num == 8) {
                    arg = arg.replace(key, this.var8);
                    continue;
                }
                if (num == 9) {
                    arg = arg.replace(key, this.var9);
                    continue;
                }
                try {
                    String replacer = m.group(num);
                    arg = arg.replace(key, replacer.replaceAll("\\\\", "\\\\\\\\"));
                    continue;
                }
                catch (IndexOutOfBoundsException ex) {
                    throw new InvalidParameterException("Unmatched reference " + key + " into " + arg);
                }
                catch (IllegalStateException ex) {
                    throw new InvalidParameterException("Unmatched reference " + key + " into " + arg);
                }
            }
            catch (NullPointerException e) {
                continue;
            }
        }
        return arg;
    }

    private int getAction(String prop) {
        if (prop.equalsIgnoreCase("insert")) {
            return 0;
        }
        if (prop.equalsIgnoreCase("set_label")) {
            return 1;
        }
        if (prop.equalsIgnoreCase("replace_matched")) {
            return 2;
        }
        if (prop.equalsIgnoreCase("replaceall_matched")) {
            return 3;
        }
        if (prop.equalsIgnoreCase("replace_content")) {
            return 4;
        }
        if (prop.equalsIgnoreCase("remove_matched")) {
            return 40;
        }
        if (prop.equalsIgnoreCase("removeall_matched")) {
            return 41;
        }
        if (prop.equalsIgnoreCase("remove_entry")) {
            return 42;
        }
        if (prop.equalsIgnoreCase("store_%8")) {
            return 43;
        }
        if (prop.equalsIgnoreCase("store_%9")) {
            return 44;
        }
        return -1;
    }

    private int getProp(String prop) {
        if (prop.equalsIgnoreCase("label")) {
            return 0;
        }
        if (prop.equalsIgnoreCase("code_regex")) {
            return 1;
        }
        if (prop.equalsIgnoreCase("name_adr")) {
            return 2;
        }
        if (prop.equalsIgnoreCase("name_hid")) {
            return 3;
        }
        if (prop.equalsIgnoreCase("parent_label")) {
            return 4;
        }
        if (prop.equalsIgnoreCase("parent_type")) {
            return 5;
        }
        if (prop.equalsIgnoreCase("code_regex_not")) {
            return 6;
        }
        if (prop.equalsIgnoreCase("parent_adr")) {
            return 7;
        }
        if (prop.equalsIgnoreCase("parent_hid")) {
            return 8;
        }
        return -1;
    }

    private int getType(String type) {
        if (type.equalsIgnoreCase("DefinitionBlock")) {
            return 0;
        }
        if (type.equalsIgnoreCase("Scope")) {
            return 1;
        }
        if (type.equalsIgnoreCase("Method")) {
            return 2;
        }
        if (type.equalsIgnoreCase("Device")) {
            return 3;
        }
        if (type.equalsIgnoreCase("Processor")) {
            return 4;
        }
        if (type.equalsIgnoreCase("ThermalZone")) {
            return 6;
        }
        if (type.equalsIgnoreCase("All")) {
            return 100;
        }
        return -1;
    }

    public static void main(String[] args) {
    }
}

