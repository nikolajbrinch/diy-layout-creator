package org.diylc.core.components.registry;

import java.util.HashMap;
import java.util.Map;

public class ComponentLookup {

    private Map<String, String> classToId = new HashMap<>();

    private Map<String, String> idToClass = new HashMap<>();

    public ComponentLookup() {
        classToId.put("org.diylc.components.arduino.NanoV3", "6a4967b3-0fc9-4a07-b0a1-12e107401457");
        classToId.put("org.diylc.components.arduino.ProMini", "3c4c9a77-2e55-4591-93cb-00c7f6be8564");
        classToId.put("org.diylc.components.boards.BlankBoard", "fa60e2a4-5f68-4f82-b9ff-604e7843c502");
        classToId.put("org.diylc.components.boards.Breadboard", "8b33c35d-35c8-4bb1-a87b-e6dd845c272a");
        classToId.put("org.diylc.components.boards.EyeletBoard", "7acf925a-8823-4005-86d7-6390a1dfea29");
        classToId.put("org.diylc.components.boards.MarshallPerfBoard", "49ec271c-36b6-4076-86e3-5d26ef5e5562");
        classToId.put("org.diylc.components.boards.PerfBoard", "53364be0-9655-4654-b2bf-786b2a4b5bca");
        classToId.put("org.diylc.components.boards.ProtoBoard", "c40cb14d-d6ac-49eb-9a9b-5083f09f74e8");
        classToId.put("org.diylc.components.boards.TriPadBoard", "7be31257-4468-40b1-a16e-5ff984bd4c54");
        classToId.put("org.diylc.components.boards.VeroBoard", "fddf7b10-660d-4b2d-9716-f283339302ac");
        classToId.put("org.diylc.components.connectivity.CopperTrace", "d8063ff1-b2de-4bdf-9818-6cd0a472094b");
        classToId.put("org.diylc.components.connectivity.CurvedTrace", "7d7ceeb2-4723-4a9e-b7c3-ae328b919b06");
        classToId.put("org.diylc.components.connectivity.Dot", "edbb23e2-bd6e-49aa-9734-882ad6da0fd4");
        classToId.put("org.diylc.components.connectivity.Eyelet", "5dc391d7-399c-4bde-99f0-e49bc1963a9c");
        classToId.put("org.diylc.components.connectivity.FemaleMolex8981", "cf3fd8dc-55b3-47f6-af1d-0911cde0a15b");
        classToId.put("org.diylc.components.connectivity.FemalePinHeader", "d10a0b9c-b7f5-4e1b-aa43-f777c3d97ecb");
        classToId.put("org.diylc.components.connectivity.GroundFill", "1beabe8c-44e4-42fa-9216-080d97d1856c");
        classToId.put("org.diylc.components.connectivity.HookupWire", "7e1a1978-0cc8-4a6a-8ba7-fd49997311e7");
        classToId.put("org.diylc.components.connectivity.Jumper", "3ea8ad8f-fc99-4712-9569-9b028c5cc6a5");
        classToId.put("org.diylc.components.connectivity.Line", "eed5c764-569b-494a-b16c-f0766142886e");
        classToId.put("org.diylc.components.connectivity.MaleMolex6410", "6b71ef92-6c33-4508-865c-b43735d933d5");
        classToId.put("org.diylc.components.connectivity.MalePinHeader", "591b66ac-fbd6-4572-ad65-acadcb2cccd7");
        classToId.put("org.diylc.components.connectivity.SolderPad", "066375ac-eb5b-4535-83d5-53c91b3f1462");
        classToId.put("org.diylc.components.connectivity.TraceCut", "68b2c265-423d-4d1a-b7a7-33f0c8a55067");
        classToId.put("org.diylc.components.electromechanical.BatterySnap9V", "63a0cec5-f823-4382-b7c0-2295d95134a6");
        classToId.put("org.diylc.components.electromechanical.CliffJack1_4", "874e9287-f861-493c-882a-8081970a82bc");
        classToId.put("org.diylc.components.electromechanical.ClosedJack1_4", "5f32c0a3-36d4-48f6-84fc-181177c68ae2");
        classToId.put("org.diylc.components.electromechanical.MiniRelay", "42fd8051-0f40-4128-9651-ac451da979bd");
        classToId.put("org.diylc.components.electromechanical.MiniToggleSwitch", "582c5b97-45e0-407b-a6f7-aa24f329af6d");
        classToId.put("org.diylc.components.electromechanical.PlasticDCJack", "7d8f485f-fa1d-42bf-a871-e095a866cf22");
        classToId.put("org.diylc.components.guitar.HumbuckerPickup", "f83986c2-ef29-4244-a6db-c50ecd4778fd");
        classToId.put("org.diylc.components.guitar.LeverSwitch", "8a06a2e4-0d25-4f3f-8d29-d030f45cab1e");
        classToId.put("org.diylc.components.guitar.SingleCoilPickup", "24f289b3-fa4a-46f3-8d33-3b2e2b702cea");
        classToId.put("org.diylc.components.misc.BOM", "7397c3fe-ef85-486e-a13f-096fb28727c8");
        classToId.put("org.diylc.components.misc.GroundSymbol", "9670c1e9-b509-4c1d-ab06-d0db4561d4ac");
        classToId.put("org.diylc.components.misc.Image", "82b3adb8-8c4e-48c2-8523-e2d08637fb1c");
        classToId.put("org.diylc.components.misc.Label", "be45857e-5b18-4549-92a1-fe21d0dea9f7");
        classToId.put("org.diylc.components.misc.PCBText", "b96441a5-2991-4c3e-bfea-5e49d116ba54");
        classToId.put("org.diylc.components.passive.AxialElectrolyticCapacitor", "513075ee-b4b5-4850-afbc-4353b231c5fe");
        classToId.put("org.diylc.components.passive.AxialFilmCapacitor", "76fd7d46-587d-431d-b52b-1c008c928b7a");
        classToId.put("org.diylc.components.passive.CapacitorSymbol", "86533907-680b-4d8c-8a81-4d49fa320581");
        classToId.put("org.diylc.components.passive.InductorSymbol", "b0b6ddb2-98b3-48fb-82af-4431836f5be0");
        classToId.put("org.diylc.components.passive.PotentiometerPanel", "40127c86-2949-479a-a537-9c86be087e85");
        classToId.put("org.diylc.components.passive.RadialCeramicDiskCapacitor", "dd6eb178-4d15-40d8-9375-609755a60411");
        classToId.put("org.diylc.components.passive.RadialElectrolytic", "63ae8eab-c916-4d93-97b2-469597034ba0");
        classToId.put("org.diylc.components.passive.RadialFilmCapacitor", "73226f94-48d4-4b8b-88a6-9de4652740b8");
        classToId.put("org.diylc.components.passive.Resistor", "bb61d88a-55fa-4c74-ad91-abc4df7a2eba");
        classToId.put("org.diylc.components.passive.ResistorSymbol", "8d9ceeae-f01e-41b1-af46-fb8bbec5a42e");
        classToId.put("org.diylc.components.passive.TrimmerPotentiometer", "dbbbd5f9-8d16-4268-ad52-8944ba3d2e08");
        classToId.put("org.diylc.components.semiconductors.BJTSymbol", "a0d13aaa-69cc-43dd-9814-a3bdc1d8b0be");
        classToId.put("org.diylc.components.semiconductors.DIL_IC", "4f198648-a374-4fd6-ac9f-f45f3f7adcda");
        classToId.put("org.diylc.components.semiconductors.DiodePlastic", "3a0eb638-786d-421e-87d1-3711f9f84854");
        classToId.put("org.diylc.components.semiconductors.DiodeSymbol", "5bb24cb5-09a9-4681-9a9f-0b3db6d482b2");
        classToId.put("org.diylc.components.semiconductors.ICSymbol", "710e39a6-5e08-4a6c-9abd-85a88aa669ba");
        classToId.put("org.diylc.components.semiconductors.JFETSymbol", "1bc71fa0-5d4f-4448-9a85-9e31d0445f06");
        classToId.put("org.diylc.components.semiconductors.LED", "a3c87f93-3fa9-4924-bf81-d71920e7a713");
        classToId.put("org.diylc.components.semiconductors.LEDSymbol", "7cf93d8d-bdc5-4078-9dbb-94d340fac3de");
        classToId.put("org.diylc.components.semiconductors.MOSFETSymbol", "dd6af7f1-3c1f-4d51-a4ef-8f59f8d70752");
        classToId.put("org.diylc.components.semiconductors.OpAmpSymbol", "5548072f-1707-4651-afc0-815dd7eda6ae");
        classToId.put("org.diylc.components.semiconductors.SIL_IC", "6b933ee9-d40b-4c00-831f-7416a7bb74f9");
        classToId.put("org.diylc.components.semiconductors.SchottkyDiodeSymbol", "c0bdc13f-a9f8-4107-b3cd-768383d313c0");
        classToId.put("org.diylc.components.semiconductors.TransistorTO1", "a17faf6d-4286-4a7e-a82b-05e2aee5d502");
        classToId.put("org.diylc.components.semiconductors.TransistorTO220", "530347e7-9b76-4ae2-9698-433a4717536b");
        classToId.put("org.diylc.components.semiconductors.TransistorTO3", "7ab351f8-2226-4b6d-bde5-2dfb9fef5e38");
        classToId.put("org.diylc.components.semiconductors.TransistorTO92", "4bb4955d-7606-411e-8ccb-5095ca2ede4a");
        classToId.put("org.diylc.components.semiconductors.ZenerDiodeSymbol", "0f9d914c-f6ba-40b3-a3c7-5982903825ba");
        classToId.put("org.diylc.components.shapes.Ellipse", "328c2d67-51ee-4530-b0cc-41d20d4158e7");
        classToId.put("org.diylc.components.shapes.Polygon", "c96fdbc4-1831-4a4b-b6c4-1c512f694500");
        classToId.put("org.diylc.components.shapes.Rectangle", "dbf3323b-d17b-4561-bde1-c654ec615a36");
        classToId.put("org.diylc.components.tube.DiodeSymbol", "5cea1730-5662-405d-8cd5-85f8fe45f26e");
        classToId.put("org.diylc.components.tube.PentodeSymbol", "31251420-a37f-49b5-954e-6927b76be94d");
        classToId.put("org.diylc.components.tube.TriodeSymbol", "dae9ad67-a50b-480e-802f-9b451ae6cf78");
        classToId.put("org.diylc.components.tube.TubeSocket", "cd07cf68-113f-490e-9015-1f91a7ad5040");

        idToClass.put("6a4967b3-0fc9-4a07-b0a1-12e107401457", "org.diylc.components.arduino.NanoV3");
        idToClass.put("3c4c9a77-2e55-4591-93cb-00c7f6be8564", "org.diylc.components.arduino.ProMini");
        idToClass.put("fa60e2a4-5f68-4f82-b9ff-604e7843c502", "org.diylc.components.boards.BlankBoard");
        idToClass.put("8b33c35d-35c8-4bb1-a87b-e6dd845c272a", "org.diylc.components.boards.Breadboard");
        idToClass.put("7acf925a-8823-4005-86d7-6390a1dfea29", "org.diylc.components.boards.EyeletBoard");
        idToClass.put("49ec271c-36b6-4076-86e3-5d26ef5e5562", "org.diylc.components.boards.MarshallPerfBoard");
        idToClass.put("53364be0-9655-4654-b2bf-786b2a4b5bca", "org.diylc.components.boards.PerfBoard");
        idToClass.put("c40cb14d-d6ac-49eb-9a9b-5083f09f74e8", "org.diylc.components.boards.ProtoBoard");
        idToClass.put("7be31257-4468-40b1-a16e-5ff984bd4c54", "org.diylc.components.boards.TriPadBoard");
        idToClass.put("fddf7b10-660d-4b2d-9716-f283339302ac", "org.diylc.components.boards.VeroBoard");
        idToClass.put("d8063ff1-b2de-4bdf-9818-6cd0a472094b", "org.diylc.components.connectivity.CopperTrace");
        idToClass.put("7d7ceeb2-4723-4a9e-b7c3-ae328b919b06", "org.diylc.components.connectivity.CurvedTrace");
        idToClass.put("edbb23e2-bd6e-49aa-9734-882ad6da0fd4", "org.diylc.components.connectivity.Dot");
        idToClass.put("5dc391d7-399c-4bde-99f0-e49bc1963a9c", "org.diylc.components.connectivity.Eyelet");
        idToClass.put("cf3fd8dc-55b3-47f6-af1d-0911cde0a15b", "org.diylc.components.connectivity.FemaleMolex8981");
        idToClass.put("d10a0b9c-b7f5-4e1b-aa43-f777c3d97ecb", "org.diylc.components.connectivity.FemalePinHeader");
        idToClass.put("1beabe8c-44e4-42fa-9216-080d97d1856c", "org.diylc.components.connectivity.GroundFill");
        idToClass.put("7e1a1978-0cc8-4a6a-8ba7-fd49997311e7", "org.diylc.components.connectivity.HookupWire");
        idToClass.put("3ea8ad8f-fc99-4712-9569-9b028c5cc6a5", "org.diylc.components.connectivity.Jumper");
        idToClass.put("eed5c764-569b-494a-b16c-f0766142886e", "org.diylc.components.connectivity.Line");
        idToClass.put("6b71ef92-6c33-4508-865c-b43735d933d5", "org.diylc.components.connectivity.MaleMolex6410");
        idToClass.put("591b66ac-fbd6-4572-ad65-acadcb2cccd7", "org.diylc.components.connectivity.MalePinHeader");
        idToClass.put("066375ac-eb5b-4535-83d5-53c91b3f1462", "org.diylc.components.connectivity.SolderPad");
        idToClass.put("68b2c265-423d-4d1a-b7a7-33f0c8a55067", "org.diylc.components.connectivity.TraceCut");
        idToClass.put("63a0cec5-f823-4382-b7c0-2295d95134a6", "org.diylc.components.electromechanical.BatterySnap9V");
        idToClass.put("874e9287-f861-493c-882a-8081970a82bc", "org.diylc.components.electromechanical.CliffJack1_4");
        idToClass.put("5f32c0a3-36d4-48f6-84fc-181177c68ae2", "org.diylc.components.electromechanical.ClosedJack1_4");
        idToClass.put("42fd8051-0f40-4128-9651-ac451da979bd", "org.diylc.components.electromechanical.MiniRelay");
        idToClass.put("582c5b97-45e0-407b-a6f7-aa24f329af6d", "org.diylc.components.electromechanical.MiniToggleSwitch");
        idToClass.put("7d8f485f-fa1d-42bf-a871-e095a866cf22", "org.diylc.components.electromechanical.PlasticDCJack");
        idToClass.put("f83986c2-ef29-4244-a6db-c50ecd4778fd", "org.diylc.components.guitar.HumbuckerPickup");
        idToClass.put("8a06a2e4-0d25-4f3f-8d29-d030f45cab1e", "org.diylc.components.guitar.LeverSwitch");
        idToClass.put("24f289b3-fa4a-46f3-8d33-3b2e2b702cea", "org.diylc.components.guitar.SingleCoilPickup");
        idToClass.put("7397c3fe-ef85-486e-a13f-096fb28727c8", "org.diylc.components.misc.BOM");
        idToClass.put("9670c1e9-b509-4c1d-ab06-d0db4561d4ac", "org.diylc.components.misc.GroundSymbol");
        idToClass.put("82b3adb8-8c4e-48c2-8523-e2d08637fb1c", "org.diylc.components.misc.Image");
        idToClass.put("be45857e-5b18-4549-92a1-fe21d0dea9f7", "org.diylc.components.misc.Label");
        idToClass.put("b96441a5-2991-4c3e-bfea-5e49d116ba54", "org.diylc.components.misc.PCBText");
        idToClass.put("513075ee-b4b5-4850-afbc-4353b231c5fe", "org.diylc.components.passive.AxialElectrolyticCapacitor");
        idToClass.put("76fd7d46-587d-431d-b52b-1c008c928b7a", "org.diylc.components.passive.AxialFilmCapacitor");
        idToClass.put("86533907-680b-4d8c-8a81-4d49fa320581", "org.diylc.components.passive.CapacitorSymbol");
        idToClass.put("b0b6ddb2-98b3-48fb-82af-4431836f5be0", "org.diylc.components.passive.InductorSymbol");
        idToClass.put("40127c86-2949-479a-a537-9c86be087e85", "org.diylc.components.passive.PotentiometerPanel");
        idToClass.put("dd6eb178-4d15-40d8-9375-609755a60411", "org.diylc.components.passive.RadialCeramicDiskCapacitor");
        idToClass.put("63ae8eab-c916-4d93-97b2-469597034ba0", "org.diylc.components.passive.RadialElectrolytic");
        idToClass.put("73226f94-48d4-4b8b-88a6-9de4652740b8", "org.diylc.components.passive.RadialFilmCapacitor");
        idToClass.put("bb61d88a-55fa-4c74-ad91-abc4df7a2eba", "org.diylc.components.passive.Resistor");
        idToClass.put("8d9ceeae-f01e-41b1-af46-fb8bbec5a42e", "org.diylc.components.passive.ResistorSymbol");
        idToClass.put("dbbbd5f9-8d16-4268-ad52-8944ba3d2e08", "org.diylc.components.passive.TrimmerPotentiometer");
        idToClass.put("a0d13aaa-69cc-43dd-9814-a3bdc1d8b0be", "org.diylc.components.semiconductors.BJTSymbol");
        idToClass.put("4f198648-a374-4fd6-ac9f-f45f3f7adcda", "org.diylc.components.semiconductors.DIL_IC");
        idToClass.put("3a0eb638-786d-421e-87d1-3711f9f84854", "org.diylc.components.semiconductors.DiodePlastic");
        idToClass.put("5bb24cb5-09a9-4681-9a9f-0b3db6d482b2", "org.diylc.components.semiconductors.DiodeSymbol");
        idToClass.put("710e39a6-5e08-4a6c-9abd-85a88aa669ba", "org.diylc.components.semiconductors.ICSymbol");
        idToClass.put("1bc71fa0-5d4f-4448-9a85-9e31d0445f06", "org.diylc.components.semiconductors.JFETSymbol");
        idToClass.put("a3c87f93-3fa9-4924-bf81-d71920e7a713", "org.diylc.components.semiconductors.LED");
        idToClass.put("7cf93d8d-bdc5-4078-9dbb-94d340fac3de", "org.diylc.components.semiconductors.LEDSymbol");
        idToClass.put("dd6af7f1-3c1f-4d51-a4ef-8f59f8d70752", "org.diylc.components.semiconductors.MOSFETSymbol");
        idToClass.put("5548072f-1707-4651-afc0-815dd7eda6ae", "org.diylc.components.semiconductors.OpAmpSymbol");
        idToClass.put("6b933ee9-d40b-4c00-831f-7416a7bb74f9", "org.diylc.components.semiconductors.SIL_IC");
        idToClass.put("c0bdc13f-a9f8-4107-b3cd-768383d313c0", "org.diylc.components.semiconductors.SchottkyDiodeSymbol");
        idToClass.put("a17faf6d-4286-4a7e-a82b-05e2aee5d502", "org.diylc.components.semiconductors.TransistorTO1");
        idToClass.put("530347e7-9b76-4ae2-9698-433a4717536b", "org.diylc.components.semiconductors.TransistorTO220");
        idToClass.put("7ab351f8-2226-4b6d-bde5-2dfb9fef5e38", "org.diylc.components.semiconductors.TransistorTO3");
        idToClass.put("4bb4955d-7606-411e-8ccb-5095ca2ede4a", "org.diylc.components.semiconductors.TransistorTO92");
        idToClass.put("0f9d914c-f6ba-40b3-a3c7-5982903825ba", "org.diylc.components.semiconductors.ZenerDiodeSymbol");
        idToClass.put("328c2d67-51ee-4530-b0cc-41d20d4158e7", "org.diylc.components.shapes.Ellipse");
        idToClass.put("c96fdbc4-1831-4a4b-b6c4-1c512f694500", "org.diylc.components.shapes.Polygon");
        idToClass.put("dbf3323b-d17b-4561-bde1-c654ec615a36", "org.diylc.components.shapes.Rectangle");
        idToClass.put("5cea1730-5662-405d-8cd5-85f8fe45f26e", "org.diylc.components.tube.DiodeSymbol");
        idToClass.put("31251420-a37f-49b5-954e-6927b76be94d", "org.diylc.components.tube.PentodeSymbol");
        idToClass.put("dae9ad67-a50b-480e-802f-9b451ae6cf78", "org.diylc.components.tube.TriodeSymbol");
        idToClass.put("cd07cf68-113f-490e-9015-1f91a7ad5040", "org.diylc.components.tube.TubeSocket");
    }
    
    public String getComponentModelId(String className) {
        return classToId.get(className);
    }
    
    public String getComponentClassName(String id) {
        return idToClass.get(id);
    }
}
