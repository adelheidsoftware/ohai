package repository;

import static org.lwjgl.cuda.CU.CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT;
import static org.lwjgl.cuda.CU.cuDeviceComputeCapability;
import static org.lwjgl.cuda.CU.cuDeviceGet;
import static org.lwjgl.cuda.CU.cuDeviceGetAttribute;
import static org.lwjgl.cuda.CU.cuDeviceGetCount;
import static org.lwjgl.cuda.CU.cuInit;
import static org.lwjgl.system.MemoryStack.stackPush;

import compute.CUDAInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import model.gpu.GpuModel;
import model.gpu.compute.GpuComputeModel;
import model.gpu.driver.GpuDriverModel;
import model.gpu.general.GpuGeneralModel;
import model.gpu.memory.GpuMemoryModel;
import org.lwjgl.system.MemoryStack;
import oshi.SystemInfo;
import util.graphics.NvidiaUtil;
import util.graphics.vendor.GpuVendor;

public class GpuRepository {

  public List<GpuModel> getModels() {
    List<GpuModel> result = new ArrayList<>();
    var gpus = new SystemInfo().getHardware().getGraphicsCards();
    for (var gpu : gpus) {
      var vendor = getVendor(gpu.getVendor().toLowerCase());

      var general = new GpuGeneralModel.Builder(gpu.getName()).deviceId(gpu.getDeviceId())
          .vendor(vendor)
          .architecture(getArchitecture())
          .build();
      var compute = generateComputeModel();
      var memory = new GpuMemoryModel.Builder(0).build();
      var driver = new GpuDriverModel.Builder().build();
      result.add(new GpuModel(general, compute, memory, driver));
    }
    return result;
  }

  private GpuVendor getVendor(String name) { // TODO: Use vendor ID instead
    var vendorName = name.toLowerCase(Locale.US);
    if (vendorName.contains("nvidia")) {
      return GpuVendor.NVIDIA;
    } else if (vendorName.contains("amd")) {
      return GpuVendor.AMD;
    } else if (vendorName.contains("intel")) {
      return GpuVendor.INTEL;
    }
    return GpuVendor.UNKNOWN;
  }

  private String getArchitecture() {
    AtomicReference<String> result = new AtomicReference<>("Unknown");
    new CUDAInstance().run(() -> {
      try (MemoryStack stack = stackPush()) {
        var pi = stack.mallocInt(1);
        cuInit(0);
        cuDeviceGetCount(pi);
        cuDeviceGet(pi, 0);
        var device = pi.get(0);

        var pt = stack.mallocInt(1);
        cuDeviceComputeCapability(pi, pt, device);

        var major = pi.get(0);
        var minor = pt.get(0);

        result.set(NvidiaUtil.getArchitecture(major, minor));
      }
    });
    return result.get();
  }

  private GpuComputeModel generateComputeModel() { // Only works for Nvidia currently
    var builder = new GpuComputeModel.Builder();

    new CUDAInstance().run(() -> {
      try (MemoryStack stack = stackPush()) {
        var pi = stack.mallocInt(1);
        cuInit(0);
        cuDeviceGetCount(pi);
        cuDeviceGet(pi, 0);
        var device = pi.get(0);

        var pt = stack.mallocInt(1);
        cuDeviceComputeCapability(pi, pt, device);

        var major = pi.get(0);
        var minor = pt.get(0);

        cuDeviceGetAttribute(pi, CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, device);
        var cuCount = pi.get(0);

        var shaderUnitCount = NvidiaUtil.getShaderUnitsPerSM(major, minor) * cuCount;
        var tensorUnitCount = NvidiaUtil.getTensorUnitsPerSM(major, minor) * cuCount;
        var raytracingUnitCount = NvidiaUtil.getRaytracingUnitsPerSM(major, minor) * cuCount;

        builder.computeUnits(cuCount);
        builder.unifiedShaderUnits(shaderUnitCount);
        builder.tensorUnits(tensorUnitCount);
        builder.raytracingUnits(raytracingUnitCount);
        builder.rasterOperationUnits(-1); // Don't know how to get these yet
        builder.textureMappingUnits(-1); // Don't know how to get these yet

      }
    });

    return builder.build();
  }

}
