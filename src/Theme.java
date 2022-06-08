public class Theme {
    private String[] letters;

    public Theme()
    {
        int randNum = (int)(Math.random()*3)+1;
        if(randNum == 1)
        {
           letters = new String[]{
                "#E6DB74", // untyped text color
                "#E2E2DC", // typed text color
                "#F92672", // incorrect text color
                "#272822"  // background color
            };
            
        }else if(randNum == 2)
        {
            //white
            letters = new String[]
            {
                "#000000", // untyped text color BLACK
                "#0000FF", // typed text color BLUE
                "#F92672", // incorrect text color RED
                "#FFFFFF"  // background color WHITE
            };
        }else
        {
            //gray
            letters = new String[]
            {
                "#34282C", // untyped text color CHARCOAL
                "#7FFD4", // typed text color AQUAMARINE
                "#F92672", // incorrect text color RED
                "#808080"  // background color GRAY
            };
        }


    }

    public String[] getTheme()
    {
        return letters;
    }
}
