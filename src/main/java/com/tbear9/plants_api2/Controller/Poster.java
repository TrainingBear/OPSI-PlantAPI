package com.tbear9.plants_api2.Controller;

import com.tbear9.plants_api2.*;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/upload/")
public class Poster {
    @PostMapping("predoct")
    public Response postSoil(@RequestBody UserVariable data){
        byte[] image = data.getImage();
        float[] prediction = FAService.predict(image);
        int max = FAService.argmax(prediction);
        Parameters.SoilParameters soil = FAService.soil[max];
        String soilName = FAService.label[max];
        data.modify(soil);

        Map<Integer, Set<CSVRecord>> result = DB.ecoCropDB_csv(data);
        Response response = Response.builder().soilName(soilName).build();
        for (int i : result.keySet()) {
            for (CSVRecord ecorecord : result.get(i)) {
                CSVRecord perawatanrecord = DB.perawatan_csv(ecorecord);
                StringBuilder querry = new StringBuilder("Tolong buat kesimpulan perawatan tanaman sesuai dengan data yang kita peroleh:  \n");
                querry.append('\n');
                if(perawatanrecord != null){
                    String perawatan = perawatanrecord.get(E.PERAWATAN);
                    String penyakit = perawatanrecord.get(E.PENYAKIT);
                    String nama_tanaman = perawatanrecord.get(E.NAME);
                    querry.append("Nama tanaman: ").append(nama_tanaman).append("\n");
                    querry.append("Perawatan: ").append(perawatan).append("\n");
                    querry.append("Penyakit: ").append(penyakit).append("\n");
                }
                else {
                    String nama_ilmiah = ecorecord.get(E.Science_name);
                }
            }
        }
        response.getParameters().put("query", querry.toString());
    }
}
