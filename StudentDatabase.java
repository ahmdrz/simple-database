
public class StudentDatabase implements AutoCloseable,java.io.Closeable {
    private static final int MAX_LENGTH = 100;
    private static final String CODE_STRING = "$%@";
    private static final int RECORD_SIZE = 2 * MAX_LENGTH + 4 + 4;
    private volatile java.io.RandomAccessFile raf;
    private String file;

    public StudentDatabase(String file) throws java.io.IOException {
        this(file,true);
    }

    public StudentDatabase(String file,boolean reset) throws java.io.IOException {
        raf = new java.io.RandomAccessFile(file,"rw");
        if (reset) raf.setLength(0);
        this.file = file;
    }

    public boolean delete() throws java.io.IOException {
        raf.setLength(0);
        close();
        java.io.File f = new java.io.File(file);
        return f.delete();
    }

    public void backup() throws java.io.IOException {
        int i=0;
        while(true) {
            java.io.File b = new java.io.File("backup" + i + "-" + this.file);
            if (!b.exists()) break;
            i++;
        }
        backup("backup" + i + "-" + this.file);
    }

    public void backup(String file) throws java.io.IOException {
        java.io.File b = new java.io.File(file);
        final boolean fileDeleted = b.delete();
        System.out.println(fileDeleted);
        raf.close();
        java.io.FileInputStream fis = new java.io.FileInputStream(this.file);
        java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
        try {
            int n;
            while ((n = fis.read()) != -1) {
                fos.write(n);
            }
        } catch (java.io.EOFException e) {
            fis.close();
            fos.close();
        }
        raf = new java.io.RandomAccessFile(this.file,"rw");
    }

    private void seek(long i) throws java.io.IOException {
        raf.seek(i);
    }

    public Student getStudent(int index) throws java.io.IOException {
        seek(RECORD_SIZE * index);
        Student s = new Student();
        s.stdNo = raf.readInt();
        s.avg = raf.readFloat();
        s.name = "";
        for (int i=0;i<MAX_LENGTH;i++)
            s.name += raf.readChar();
        s.name = decodeString(s.name);
        return s;
    }

    public void setStudent(Student s,int index) throws java.io.IOException {
        checkIndex(index,length());
        seek(RECORD_SIZE * index);
        raf.writeInt(s.stdNo);
        raf.writeFloat(s.avg);
        raf.writeChars(encodeString(s.name));
    }

    public void insertStudent(Student s) throws java.io.IOException {
        insertStudent(s,length());
    }

    public void removeStudent(int index) throws java.io.IOException {
        checkIndex(index,length());
        for (int i=index;i<length() - 1;i++)
            setStudent(getStudent(i+1),i);
        raf.setLength(RECORD_SIZE * (length() - 1));
    }

    public void removeStudent(Student s) throws java.io.IOException {
        removeStudent(indexOf(s));
    }

    public void insertStudent(Student s,int index) throws java.io.IOException {
        int length = length();
        checkIndex(index,length + 1);
        ensureCapacity(length + 1);
        for (int i=index;i<length;i++)
            setStudent(getStudent(i),i+1);

        setStudent(s,index);
    }

    private void ensureCapacity(int minCapacity) throws java.io.IOException {
        if (minCapacity > length())
            raf.setLength(minCapacity * RECORD_SIZE);
    }

    private void checkIndex(int index,int size) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("No index founded!");
        }
    }

    public int length() throws java.io.IOException {
        return (int)(raf.length() / RECORD_SIZE);
    }

    public int indexOf(Student s) throws java.io.IOException {
        for (int i=0;i<length();i++)
            if (s.equals(getStudent(i)))
                return i;
        return -1;
    }

    public void swap(int index1,int index2)  throws java.io.IOException {
        Student t = getStudent(index1);
        setStudent(getStudent(index2),index1);
        setStudent(t,index2);
    }

    public void sort() throws java.io.IOException {
        sort(0,length() - 1);
    }

    public void sort(int lowerIndex,int higherIndex) throws java.io.IOException {
        checkIndex(lowerIndex,length());
        checkIndex(higherIndex,length());
        int i = lowerIndex;
        int j = higherIndex;
        Student m = getStudent(lowerIndex + (higherIndex - lowerIndex)/2);
        while (i <= j) {
            while(getStudent(i).compareTo(m) > 0)
                i++;

            while (getStudent(j).compareTo(m) < 0)
                j--;

            if (i <= j) {
                swap(i,j);
                i++;
                j--;
            }
        }

        if (lowerIndex < j)
            sort(lowerIndex,j);
        if (i < higherIndex)
            sort(i,higherIndex);
    }

    private String encodeString(String s) {
        if (s.length() > MAX_LENGTH)
            throw new StringIndexOutOfBoundsException();
        int length = MAX_LENGTH - s.length() - CODE_STRING.length();
        if (length <= 0)
            throw new StringIndexOutOfBoundsException();

        String result = "";
        for (int i = 0; i < length; i++)
            result += " ";
        result += CODE_STRING + s;
        return result;
    }

    private String decodeString(String s) {
        return s.substring(s.indexOf(CODE_STRING) + CODE_STRING.length(),s.length());
    }

    public boolean contains(Student s) throws java.io.IOException {
        for (int i=0;i<length();i++) {
            if (s.equals(getStudent(i)))
                return true;
        }
        return false;
    }

    @Override
    public void close() throws java.io.IOException {
        raf.close();
    }
}