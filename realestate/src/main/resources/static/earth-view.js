function addRealWorldLayers(viewer) {
    const credit = "Imagery © Esri, Maxar, Earthstar Geographics. Roads and labels © Esri.";
    viewer.imageryLayers.addImageryProvider(new Cesium.UrlTemplateImageryProvider({
        url: "https://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
        maximumLevel: 19,
        credit: credit
    }));
    viewer.imageryLayers.addImageryProvider(new Cesium.UrlTemplateImageryProvider({
        url: "https://services.arcgisonline.com/ArcGIS/rest/services/Reference/World_Transportation/MapServer/tile/{z}/{y}/{x}",
        maximumLevel: 19
    }));
    viewer.imageryLayers.addImageryProvider(new Cesium.UrlTemplateImageryProvider({
        url: "https://services.arcgisonline.com/ArcGIS/rest/services/Reference/World_Boundaries_and_Places/MapServer/tile/{z}/{y}/{x}",
        maximumLevel: 19
    }));
}

function lookAtGround(viewer, lng, lat, range) {
    viewer.camera.flyToBoundingSphere(
        new Cesium.BoundingSphere(Cesium.Cartesian3.fromDegrees(lng, lat, 0), 30),
        {
            offset: new Cesium.HeadingPitchRange(
                Cesium.Math.toRadians(28),
                Cesium.Math.toRadians(-42),
                range || 420
            ),
            duration: 2.2
        }
    );
}
