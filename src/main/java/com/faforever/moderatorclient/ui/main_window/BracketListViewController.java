package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.caches.SmallThumbnailCache;
import com.faforever.moderatorclient.ui.data_cells.ListViewMapCell;
import com.faforever.moderatorclient.ui.domain.MapPoolAssignmentFX;
import com.faforever.moderatorclient.ui.domain.MatchmakerQueueMapPoolFX;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BracketListViewController implements Controller<VBox> {

    public Spinner<Integer> vetoTokensPerPlayerSpinner;
    public Spinner<Integer> maxTokensPerMapSpinner;
    public Spinner<Double> minimumMapsAfterVetoSpinner;
    public CheckBox dynamicMaxTokensPerMapCheckBox;
    public HBox maxTokensPerMapHBox;
    @FXML
    VBox root;
    @FXML
    ListView<MapPoolAssignmentFX> mapListView;

    private final SmallThumbnailCache smallThumbnailCache;

    @Override
    public VBox getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        mapListView.setCellFactory(mapListView -> new ListViewMapCell(smallThumbnailCache));
        vetoTokensPerPlayerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0, 1));
        maxTokensPerMapSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,255,1,1));
        minimumMapsAfterVetoSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.001, 255, 1, 0.5));
    }

    public void setMaps(ObservableList<MapPoolAssignmentFX> maps) {
        mapListView.prefHeightProperty().bind(Bindings.size(maps).multiply(70));
        mapListView.setItems(maps);
//        mapListView.setItems(maps.sorted(Comparator.comparing(MapPoolAssignmentFX::getId)));
    }

    public void bindVetoParams(MatchmakerQueueMapPoolFX bracket) {
        dynamicMaxTokensPerMapCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                log.debug(String.valueOf(bracket.maxTokensPerMapProperty().get()));
                maxTokensPerMapSpinner.setDisable(true);
                bracket.maxTokensPerMapProperty().set(0);
            } else {
                bracket.maxTokensPerMapProperty().set(1);
                maxTokensPerMapSpinner.setDisable(false);
            }
        });

        dynamicMaxTokensPerMapCheckBox.setSelected(bracket.maxTokensPerMapProperty().get() == 0);

        maxTokensPerMapSpinner.getValueFactory().setValue(bracket.maxTokensPerMapProperty().get());
        maxTokensPerMapSpinner.valueProperty().addListener((observable, oldValue, newValue) -> bracket.maxTokensPerMapProperty().set(newValue));

        vetoTokensPerPlayerSpinner.getValueFactory().setValue(bracket.vetoTokensPerPlayerProperty().get());
        vetoTokensPerPlayerSpinner.valueProperty().addListener((observable, oldValue, newValue) -> bracket.vetoTokensPerPlayerProperty().set(newValue));

        minimumMapsAfterVetoSpinner.getValueFactory().setValue(bracket.minimumMapsAfterVetoProperty().get());
        minimumMapsAfterVetoSpinner.valueProperty().addListener((observable, oldValue, newValue) -> bracket.minimumMapsAfterVetoProperty().set(newValue));
    }
}
