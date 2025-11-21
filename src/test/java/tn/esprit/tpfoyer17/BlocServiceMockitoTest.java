package tn.esprit.tpfoyer17;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;
import tn.esprit.tpfoyer17.services.BlocService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
 class BlocServiceMockitoTest {

    @InjectMocks
    BlocService blocService;

    @Mock
    BlocRepository blocRepository;

    @Mock
    ChambreRepository chambreRepository;

    Bloc sampleBloc;
    List<Chambre> sampleChambres;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleBloc = new Bloc();
        sampleBloc.setNomBloc("Bloc A");
        sampleBloc.setCapaciteBloc(150);

        Chambre c1 = new Chambre();
        c1.setNumeroChambre(101L);

        Chambre c2 = new Chambre();
        c2.setNumeroChambre(102L);

        sampleChambres = Arrays.asList(c1, c2);
    }

   @Test
 void testAffecterChambresABloc_success() {

    when(blocRepository.findById(1L)).thenReturn(Optional.of(sampleBloc));
    when(chambreRepository.findAllById(Arrays.asList(101L, 102L))).thenReturn(sampleChambres);

    Bloc result = blocService.affecterChambresABloc(Arrays.asList(101L, 102L), 1L);

    // Should save both chambres
    verify(chambreRepository, times(2)).save(any(Chambre.class));

    // blocRepository.findById is called twice in the service
    verify(blocRepository, times(2)).findById(1L);

    assertEquals(150, result.getCapaciteBloc());
}

} // webhook test

