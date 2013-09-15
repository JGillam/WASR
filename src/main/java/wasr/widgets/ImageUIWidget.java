/*
 * Copyright 2013 Jason Gillam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wasr.widgets;

import org.apache.log4j.Logger;
import wasr.Icons;
import wasr.WASRFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class ImageUIWidget extends FieldUIWidget {
    private static Logger LOG = Logger.getLogger(ImageUIWidget.class);
    JPanel imagePanel = new JPanel(new BorderLayout());
    JLabel imageLabel = new JLabel();
    BufferedImage image = null;
    Pattern imageFilePattern = Pattern.compile("^image_[0-9]{3}\\.png$");
    DecimalFormat formatter = new DecimalFormat("000");
    String imageURL;

    public ImageUIWidget(String nodeId, String fieldId) {
        super(nodeId, fieldId);
        LOG.debug("Constructing widget for " + fieldId);
        imageLabel.setIcon(getDefaultImage());
        imagePanel.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        JToolBar bar = new JToolBar(JToolBar.VERTICAL);
        bar.setFloatable(false);
        //bar.add(makePasteAction());    //todo: Paste from clipboard not working on mac
        bar.add(makeLoadImageAction());

        imagePanel.add(bar, BorderLayout.WEST);
    }


    private Action makeLoadImageAction() {
        Action a = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes());
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(WASRFrame.getInstance());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File inputFile = chooser.getSelectedFile();
                    File outputFile = generateImageFile();
                    if (inputFile.exists()) {
                        try {
                            image = ImageIO.read(inputFile);
                            ImageIO.write(image, "PNG", outputFile);
                            imageURL = outputFile.getName();
                            imageLabel.setIcon(new ImageIcon(image));
                            imagePanel.updateUI();
                        } catch (IOException e1) {
                            LOG.warn("Error loading image from " + inputFile.getPath(), e1);
                        }
                    }
                }
            }

        };
        URL imageResource = getClass().getResource(Icons.IMAGE_LOAD_32);
        if (imageResource == null) {
            System.out.println("Image not found for " + Icons.IMAGE_LOAD_32);
        } else {
            a.putValue(Action.LARGE_ICON_KEY, new ImageIcon(imageResource));
        }
        return a;
    }

    private File generateImageFile() {
        File reportFolder = WASRFrame.getInstance().getCurrentReport().getReportFolder();
        String[] imageFiles = reportFolder.list(new FilenameFilter
                () {
            @Override
            public boolean accept(File dir, String name) {
                return imageFilePattern.matcher(name).find();
            }
        });

        if (imageFiles.length > 0) {
            Arrays.sort(imageFiles);
            String lastNum = imageFiles[imageFiles.length - 1].substring("image_".length(), imageFiles[imageFiles.length - 1].length() - ".png".length());
            int i = Integer.parseInt(lastNum) + 1;
            return new File(reportFolder, "image_" + formatter.format(i) + ".png");
        } else {
            return new File(reportFolder, "image_001.png");
        }
    }


    private Action makePasteAction() {
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemSelection();
                DataFlavor flavor = DataFlavor.imageFlavor;
                DataFlavor[] flavs = clipboard.getAvailableDataFlavors();
                LOG.debug("Number of available data flavors: " + flavs.length);
//                for(DataFlavor flav:flavs){
//                    LOG.debug("Available flavor: "+flav.getHumanPresentableName());
//                }


                Transferable contents = clipboard.getContents(this);
                flavs = contents.getTransferDataFlavors();
                LOG.debug("Number of available data flavors (contents): " + flavs.length);
//                for(DataFlavor flav:flavs){
//                    LOG.debug("Available flavor: "+flav.getHumanPresentableName());
//                }


                LOG.debug("Dataflavor available on contents: " + contents.isDataFlavorSupported(flavor));


                //if (clipboard.isDataFlavorAvailable(flavor)) {
                try {
                    image = (BufferedImage) clipboard.getData(flavor);
                    //image = (BufferedImage) contents.getTransferData(flavor);
                    imageLabel.setIcon(new ImageIcon(image));
                    imagePanel.updateUI();
                    LOG.debug("Pasted image: " + ImageUIWidget.this.getValue());
                } catch (UnsupportedFlavorException exception) {
                    LOG.warn(exception);
                } catch (IOException exception) {
                    LOG.error(exception);
                }
                //} else {
                //    LOG.debug("Attempt to paste with no image on the clipboard");
                //}
            }
        };
        URL imageResource = getClass().getResource(Icons.PASTE_16);
        if (imageResource == null) {
            System.out.println("Image not found for " + Icons.PASTE_16);
        } else {
            a.putValue(Action.LARGE_ICON_KEY, new ImageIcon(imageResource));
        }
        return a;
    }

    private Icon getDefaultImage() {
        URL imageResource = getClass().getResource(Icons.IMAGE_DEFAULT_32);
        return new ImageIcon(imageResource);
    }

    @Override
    public JComponent getComponent() {
        return imagePanel;
    }

    @Override
    public String getValue() {
        return imageURL;
    }


    @Override
    public void setValue(String value) {
        File imageFile = new File(WASRFrame.getInstance().getCurrentReport().getReportFolder(), value);
        if (imageFile.exists()) {
            try {
                image = ImageIO.read(imageFile);
                imageLabel.setIcon(new ImageIcon(image));
                imageURL = value;
                imagePanel.updateUI();
            } catch (IOException e) {
                imageURL = "";
                imageLabel.setIcon(getDefaultImage());  // TODO: display some kind of warning
            }
        } else {
            imageURL = "";
            imageLabel.setIcon(getDefaultImage());  // TODO: display some kind of warning
        }

    }
}
