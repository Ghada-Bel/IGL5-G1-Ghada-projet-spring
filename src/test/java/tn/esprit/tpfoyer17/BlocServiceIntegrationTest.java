package tn.esprit.tpfoyer17;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;
import tn.esprit.tpfoyer17.services.IBlocService;

import java.util.List;
import java.util.Set;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY) // use embedded DB for tests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
 class BlocServiceIntegrationTest {

    @Autowired
    IBlocService blocService;

    @Autowired
    BlocRepository blocRepository;

    @Autowired
    ChambreRepository chambreRepository;

    static long createdBlocId;

    @Test
    @Order(1)
     void testAddBloc() {
        Bloc b = Bloc.builder()
                .nomBloc("Bloc-Test")
                .capaciteBloc(10L)
                .build();

        Bloc saved = blocService.addBloc(b);
        assertNotNull(saved);
        assertTrue(saved.getIdBloc() > 0);
        createdBlocId = saved.getIdBloc();
    }

    @Test
    @Order(2)
     void testGetBloc() {
        Bloc found = blocService.getBlocById(createdBlocId);
        assertNotNull(found);
        assertEquals("Bloc-Test", found.getNomBloc());
    }

    @Test
    @Order(3)
     void testUpdateBloc() {
        Bloc bloc = blocService.getBlocById(createdBlocId);
        bloc.setCapaciteBloc(20L);
        Bloc updated = blocService.updateBloc(bloc);
        assertEquals(20L, updated.getCapaciteBloc());
    }

    @Test
    @Order(4)
     void testDeleteBloc() {
        // create a temporary bloc to delete
        Bloc toDelete = Bloc.builder().nomBloc("ToDelete").capaciteBloc(1L).build();
        Bloc saved = blocService.addBloc(toDelete);
        long idToDelete = saved.getIdBloc();
        blocService.deleteBloc(idToDelete);
        assertFalse(blocRepository.existsById(idToDelete));
    }
}
