
#include <stdio.h>

int main(int argc, char *argv[]) {
    FILE *f;
    char buffer[100] = "";
    char outfile[100];

    if (argc <= 1) {
       return 1;
    }

    sprintf(outfile, "%s\\getjava.out", argv[1]);
    f = fopen(outfile, "wt");
    printf("Please enter the directory where java.exe is installed: ");
    fgets(buffer, 100, stdin);
    fputs(buffer, f);
    fclose(f);

    return 0;
}

