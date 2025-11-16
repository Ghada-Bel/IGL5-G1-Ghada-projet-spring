package tn.esprit.tpfoyer17.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpfoyer17.entities.Bloc;
import tn.esprit.tpfoyer17.entities.Chambre;
import tn.esprit.tpfoyer17.repositories.BlocRepository;
import tn.esprit.tpfoyer17.repositories.ChambreRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlocService implements IBlocService {

    BlocRepository blocRepository;
    ChambreRepository chambreRepository;

    @Override
    public Bloc addBloc(Bloc bloc) {
        log.info("addBloc - called with bloc: {}", bloc);
        Bloc saved = blocRepository.save(bloc);
        log.info("addBloc - saved bloc with id {}", saved.getIdBloc());
        return saved;
    }

    @Override
    public List<Bloc> getAllBlocs() {
        log.debug("getAllBlocs - fetching all blocs");
        List<Bloc> blocs = (List<Bloc>) blocRepository.findAll();
        log.info("getAllBlocs - found {} blocs", blocs.size());
        return blocs;
    }

    @Override
    public Bloc getBlocById(long idBloc) {
        log.info("getBlocById - looking for bloc id={}", idBloc);
        return blocRepository.findById(idBloc)
                .orElseThrow(() -> {
                    log.error("getBlocById - bloc not found id={}", idBloc);
                    return new RuntimeException("Bloc not found: " + idBloc);
                });
    }

    @Override
    public void deleteBloc(long idBloc) {
        log.info("deleteBloc - deleting bloc id={}", idBloc);
        if (!blocRepository.existsById(idBloc)) {
            log.warn("deleteBloc - bloc id={} does not exist", idBloc);
            return;
        }
        blocRepository.deleteById(idBloc);
        log.info("deleteBloc - deleted bloc id={}", idBloc);
    }

    @Override
    public Bloc updateBloc(Bloc bloc) {
        log.info("updateBloc - updating bloc id={}", bloc.getIdBloc());
        Bloc updated = blocRepository.save(bloc);
        log.info("updateBloc - updated bloc id={}", updated.getIdBloc());
        return updated;
    }

    /**
     * Assign a list of chambres to a bloc.
     * This operation is transactional: either all chambre assignments succeed or none.
     */
    @Override
    @Transactional
    public Bloc affecterChambresABloc(List<Long> numChambre, long idBloc) {
        log.info("affecterChambresABloc - called with numChambre={} idBloc={}", numChambre, idBloc);

        Bloc bloc = blocRepository.findById(idBloc)
                .orElseThrow(() -> {
                    log.error("affecterChambresABloc - bloc not found id={}", idBloc);
                    return new RuntimeException("Bloc not found: " + idBloc);
                });

        List<Chambre> chambres = (List<Chambre>) chambreRepository.findAllById(numChambre);
        log.info("affecterChambresABloc - found {} chambres for assignment", chambres.size());

        if (chambres.isEmpty()) {
            log.warn("affecterChambresABloc - no chambres found for ids {}", numChambre);
        }

        for (Chambre chambre : chambres) {
            try {
                chambre.setBloc(bloc);
                chambreRepository.save(chambre);
                log.debug(
                        "affecterChambresABloc - assigned chambre id={} to bloc id={}",
                        chambre.getIdChambre(),
                        bloc.getIdBloc()
                );
            } catch (Exception e) {
                log.error(
                        "affecterChambresABloc - failed to assign chambre id={} : {}",
                        chambre.getIdChambre(),
                        e.getMessage()
                );
                throw e; // rollback because @Transactional
            }
        }

        Bloc updatedBloc = blocRepository.findById(idBloc).orElse(bloc);
        log.info(
                "affecterChambresABloc - finished assigning {} chambres to bloc id={}",
                chambres.size(),
                idBloc
        );

        return updatedBloc;
    }
}
