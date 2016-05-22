public strictfp class Student implements java.io.Serializable,Comparable<Student> {
    private static transient final int CLASS_HASH = 6851;
    public int stdNo;
    public float avg;
    public String name;

    public Student(int stdNo, float avg, String name) {
        this.stdNo = stdNo;
        this.avg = avg;
        this.name = name;
    }

    public Student() {

    }

    @Override
    public boolean equals(Object obj) {
        assert obj != null;

        if (! (obj instanceof Student))
            return false;

        Student s = (Student) obj;
        return s.stdNo == stdNo && s.name.equals(name) && s.avg == avg;
    }

    @Override
    public int hashCode() {
        int hash = CLASS_HASH;
        hash = hash * 17 + stdNo;
        hash = hash * 31 + name.hashCode();
        hash = hash * 13 + (int)(avg*1000);
        return hash;
    }

    @Override
    public String toString() {
        return stdNo + " " + avg + " " + name;
    }

    @Override
    public int compareTo(Student e) {
        assert e != null;

        if (e.stdNo != this.stdNo) {
            if (e.stdNo > this.stdNo)
                return 1;
            if (e.stdNo < this.stdNo)
                return -1;
        } else if (!e.name.equals(this.name)) {
            return e.name.compareTo(this.name);
        } else {
            if (e.avg < this.avg)
                return 1;
            else if (e.avg > this.avg)
                return -1;
        }
        return 0;
    }
}
